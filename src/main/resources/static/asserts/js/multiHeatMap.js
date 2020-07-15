
$(function () {
    $('#draggable1').draggabilly({ handle: '.handle' });
});

// 自定义分辨率和瓦片坐标系
var resolutions = [];
var maxZoom = 18;
var tilegrid = new ol.tilegrid.TileGrid({
    origin: ol.proj.transform([0, 0.201], 'EPSG:4326', 'EPSG:3857'),    // 设置原点坐标
    resolutions: [
        156543.03392804097,
        78271.51696402048,
        39135.75848201024,
        19567.87924100512,
        9783.93962050256,
        4891.96981025128,
        2445.98490512564,
        1222.99245256282,
        611.49622628141,
        305.748113140705,
        152.8740565703525,
        76.43702828517625,
        38.21851414258813,
        19.109257071294063,
        9.554628535647032,
        4.777314267823516,
        2.388657133911758
    ]   // 设置分辨率
    // extent: ol.proj.transformExtent([-180.0, -90.0, 180.0, 90.0], 'EPSG:4326', 'EPSG:3857'),//范围
});

// 创建海图地图的数据源
var shipxySource = new ol.source.TileImage({
    projection: 'EPSG:3857',
    tileGrid: tilegrid,
    tileUrlFunction: function (tileCoord, pixelRatio, proj) {
        var z = tileCoord[0];
        var x = tileCoord[1];
        var y = tileCoord[2];

        if (Math.abs(x) > Math.pow(2, z) / 2) {
            return;
        }
        x = x + Math.pow(2, z) / 2;
        //起点
        var flag = Math.pow(2, z) / 2 - 1;
        if (y <= flag) {
            y = flag - y;
        } else {
            return;
        }
        var tmp = (8 - 2) * Math.pow(2, z - 3) + 2 + Math.pow(2, z - 3) - 1;
        //上边界：距离flag处z-3的i次方减一。下边界：距离上边界处6的z-3次方加2，所以下边界距离flag处为Math.pow(2,z-3)-1+6*Math.pow(2,z-3)+3
        if (y < Math.pow(2, z - 3) - 1 || y >= tmp) {
            return;
        }
        return "http://m12.shipxy.com/tile.c?l=Na&m=o&x=" + x + "&y=" + y + "&z=" + z + "";
    }
});

var shipxyLayer = new ol.layer.Tile({
    projection: 'EPSG:3857',
    source: shipxySource
    // ,wrapX: false
});
const map = new ol.Map({
    target: 'map',
    layers: [shipxyLayer
    ],
    view: new ol.View({
        projection: 'EPSG:4326',
        center: [114.3872997, 30.6695992],
        zoom: 5,
        maxZoom: 16,
        minZoom: 2
        // extent: fullExtent
    }),
    controls: ol.control.defaults({attribution: false, zoom: true, rotate: false})
// controls: ol.control.defaults({attribution: false, zoom: false, rotate: false}).extend([mousePositionControl])
});
let flag=0,sflag=0;
var linesource = new ol.source.Vector({})
var routeLength ;
//路线feature
var routeFeature;
//动画点feature
var geoMarker ;

var styles = {
    'route': new ol.style.Style({
        stroke: new ol.style.Stroke({
            width: 5,
            color: [237, 212, 0, 0.8]
        })
    }),
    'geoMarker': new ol.style.Style({
        image: new ol.style.Circle({
            radius: 6,
            snapToPixel: false,
            fill: new ol.style.Fill({
                color: 'white'
            }),
            stroke: new ol.style.Stroke({
                color: 'grey',
                width: 1
            })
        })
    })
};

var animating = false;
var speed, nowDate;

//路线动画层
var lineLayer = new ol.layer.Vector({
    source: linesource,
    style: function (feature) {
        // 如果正在动画中，隐藏动画点geoMarker
        if (animating && feature.get('type') === 'geoMarker') {
            return null;
        }
        return styles[feature.get('type')];
    }
});

//移动feature
var moveFeature;
var s,e,pause;
function run() {
    if(sflag==0){
        alert("请先选择编号中的一项")
    }else{
    if (animating) {
        stopAnimation(false);
    } else {
        //开始动画
        animating = true;
        nowDate = new Date().getTime();
        speed = 2;
        // 隐藏动画点feature
        geoMarker.setStyle(null);
        map.on('postcompose', moveFeature);
        map.render();
    }
    }
}

/**
 * 结束动画
 * @param {boolean} ended 判断动画是否取消
 */


function stopAnimation(ended) {
    if(sflag==0){
        alert("请先选择编号中的一项")
    }else {
        animating = false;
        // 如果动画取消，设置标记到开始位置
        var coord = ended ? e : s;
        /** @type {ol.geom.Point} */
        geoMarker.getGeometry().setCoordinates(coord);
        map.un('postcompose', moveFeature);
        // run()
    }
}
var heatMapsource = new ol.source.Vector({})

var heatlayer = new ol.layer.Heatmap({
    source: heatMapsource,
    blur: parseInt(15),
    radius: parseInt(5),
    opacity: 1,
    gradient: ['#eb3735','#EB312F','#EB2222','#EB1513','#EB0A0B','#EB0005']
});
map.addLayer(heatlayer);
//绘制
function draw(next) {
    $.ajax({
        url: "/ajax/draw",
        type: "POST",
        data: {
            next: next
        },
        dataType: "json",
        success: function (list) {
            let tx="_波高"+next;
            $('#bh').html(tx)
            if (flag!=0){
                heatMapsource.clear()
                linesource.removeFeature(routeFeature)
                linesource.removeFeature(geoMarker)
                linesource.clear()
            }
            draw_line(next)
            sflag=1;
            let min = 0, max = 0
            max = Math.max.apply(Math, list.map(function (o) {
                return o.shipName
            }))
            min = Math.min.apply(Math, list.map(function (o) {
                return o.shipName
            }))
            let features = []
            list.forEach(function (v) {
                let x1 = (v.shipName - min) / (max - min)
                let x2 = x1 * (1 - 0) + 0
                var feature = new ol.Feature({
                    geometry: new ol.geom.Point([v.lng, v.lat]),
                    weight: x2
                });
                features.push(feature)
            })
            heatMapsource.addFeatures(features)
            heatMapsource.refresh()


        },
        error: function () {
            alert("error");

        }
    })
}

//绘制轨迹线
function draw_line(next) {
    $.ajax({
        url: "/ajax/draw_line",
        type: "POST",
        data: {
            next: next
        },
        dataType: "json",
        success: function (list) {
            // if (flag==1){
            //     linesource.refresh()
            //     linesource.clear()
            //
            //     // map.removeLayer(lineLayer)
            // }
            sessionStorage.setItem('route', JSON.stringify(list));
            let html1 = "";
            for(let i=1;i<list.length;i++){
                html1 += "<tr><td>" + i + "</td><td>"
                    + list[i][0] + "</td><td>"
                    + list[i][1] + "</td></tr>";
            }
            $('#route').html(html1)


            routeLength = list.length;
            routeFeature = new ol.Feature({
                type: 'route',
                geometry: new ol.geom.LineString(list)
            });
//动画点feature
            geoMarker = new ol.Feature({
                type: 'geoMarker',
                geometry: new ol.geom.Point(list[0])
            });
            linesource.addFeature(routeFeature)
            linesource.addFeature(geoMarker)
            linesource.refresh()
            if (flag==0){
                map.addLayer(lineLayer)
            }
            // lineLayer.refresh()
            moveFeature = function (event) {
                var vectorContext = event.vectorContext;
                var frameState = event.frameState;

                if (animating) {
                    var elapsedTime = frameState.time - nowDate;
                    //在这里，提高速度的技巧是在lineString坐标上跳转一些索引
                    var index = Math.round(speed * elapsedTime / 1000);
                    if (index >= routeLength) {
                        //从头运行
                        stopAnimation(true);
                        return;
                    }

                    var currentPoint = new ol.geom.Point(list[index]);
                    var feature = new ol.Feature(currentPoint);
                    vectorContext.drawFeature(feature, styles.geoMarker);
                }
                //告诉OpenLayers继续postcompose动画
                map.render();
            };

            e=list[routeLength - 1]
            s=list[0]
            flag=1;

        },
        error: function () {
            alert("error");
        }
    })
}
function insert_route() {
    let lng=parseInt($('#lng1').val());
    let lat=parseInt($('#lat1').val());
    let route = JSON.parse(sessionStorage.getItem('route'));
    let new_route=new Array(2);
    new_route[0]=lng;
    new_route[1]=lat;
    route.push(new_route)
    console.log(route)
    go_line(route)
}
function modal_show() {
    $('#myModal').modal('show');

}
function go_line(list) {
    linesource.removeFeature(routeFeature)
    linesource.removeFeature(geoMarker)
    linesource.clear()
    let html1 = "";
    for(let i=1;i<list.length;i++){
        html1 += "<tr><td>" + i + "</td><td>"
            + list[i][0] + "</td><td>"
            + list[i][1] + "</td></tr>";
    }
    $('#route').html(html1)


    routeLength = list.length;
    routeFeature = new ol.Feature({
        type: 'route',
        geometry: new ol.geom.LineString(list)
    });
//动画点feature
    geoMarker = new ol.Feature({
        type: 'geoMarker',
        geometry: new ol.geom.Point(list[0])
    });
    linesource.addFeature(routeFeature)
    linesource.addFeature(geoMarker)
    linesource.refresh()
    if (flag==0){
        map.addLayer(lineLayer)
    }
    // lineLayer.refresh()
    moveFeature = function (event) {
        var vectorContext = event.vectorContext;
        var frameState = event.frameState;

        if (animating) {
            var elapsedTime = frameState.time - nowDate;
            //在这里，提高速度的技巧是在lineString坐标上跳转一些索引
            var index = Math.round(speed * elapsedTime / 1000);
            if (index >= routeLength) {
                //从头运行
                stopAnimation(true);
                return;
            }

            var currentPoint = new ol.geom.Point(list[index]);
            var feature = new ol.Feature(currentPoint);
            vectorContext.drawFeature(feature, styles.geoMarker);
        }
        //告诉OpenLayers继续postcompose动画
        map.render();
    };

    e=list[routeLength - 1]
    s=list[0]
    flag=1;
}
let po=1
let display_flag=1
let stop_flag=0;
function display() {

    if (display_flag==1){
        if(po<10){
            draw(po);
            po=po+1;
            setTimeout(display,2000)
        }else {
            po=1;
           display_flag=1
        }
    } else {
        display_flag=0
    }


}

function stop() {
    if (stop_flag%2==0) {
        $('#stop').html("继续")

        display_flag=0
    }else {
        $('#stop').html("停止")

        display_flag=1
    }
    stop_flag++;
}

