// //画断面图层
const source1 = new ol.source.Vector({wrapX: false});
//绘图绘在此矢量图层 画断面的图层，在图上画线
const vector1 = new ol.layer.Vector({
    source: source1,
    style: new ol.style.Style({ //修改绘制的样式
        fill: new ol.style.Fill({
            color: 'rgba(255, 255, 255, 0.2)'
        }),
        stroke: new ol.style.Stroke({
            color: '#ffcc33',
            width: 2
        }),
        image: new ol.style.Circle({
            radius: 7,
            fill: new ol.style.Fill({
                color: '#ffcc33'
            })
        })
    })
});

var openSeaMapLayer = new ol.layer.Tile({
    source: new ol.source.XYZ({
        opaque: false,
        url: 'https://tiles.openseamap.org/seamark/{z}/{x}/{y}.png'
    })
});


// 自定义分辨率和瓦片坐标系
var resolutions = [];
var maxZoom = 18;
var tilegrid  = new ol.tilegrid.TileGrid({
    origin: ol.proj.transform([0,0.201], 'EPSG:4326', 'EPSG:3857'),    // 设置原点坐标
    resolutions:  [
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
    tileUrlFunction: function(tileCoord, pixelRatio, proj){
        var z = tileCoord[0];
        var x = tileCoord[1];
        var y = tileCoord[2];

        if(Math.abs(x)>Math.pow(2,z)/2){
            return;
        }
        x= x+Math.pow(2,z)/2;
        //起点
        var flag = Math.pow(2,z)/2-1;
        if(y<=flag){
            y = flag-y;
        }else{
            return;
        }
        var tmp = (8-2)*Math.pow(2,z-3)+2+Math.pow(2,z-3)-1;
        //上边界：距离flag处z-3的i次方减一。下边界：距离上边界处6的z-3次方加2，所以下边界距离flag处为Math.pow(2,z-3)-1+6*Math.pow(2,z-3)+3
        if(y<Math.pow(2,z-3)-1||y>=tmp){
            return;
        }
        return "http://m12.shipxy.com/tile.c?l=Na&m=o&x="+x+"&y="+y+"&z="+z+"";
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
        , openSeaMapLayer
        , vector1
    ],
    view: new ol.View({
        projection: 'EPSG:4326',
        center: [114.3872997,30.6695992],
        zoom: 5,
        maxZoom: 16,
        minZoom: 2
        // extent: fullExtent
    }),
    controls: ol.control.defaults({attribution: false, zoom: true, rotate: false})
// controls: ol.control.defaults({attribution: false, zoom: false, rotate: false}).extend([mousePositionControl])
});


//弹框显示地理信息
let count = 1;
let clickflag=0;
map.on('singleclick', function (evt) {
    if(clickflag==1) {
        if (count % 2 == 0) {
            document.getElementById("lng2").value = evt.coordinate[0].toFixed(6);
            document.getElementById("lat2").value = evt.coordinate[1].toFixed(6);
            // console.log(ol.proj.transform([evt.coordinate[0],evt.coordinate[1]], 'EPSG:3857', 'EPSG:4326'))
            //此处需要判断绘制的断面是否在所查询的区域内
            // if(evt.coordinate[0].toFixed(6)<border_x1)
            $("#lat1").css("background-color","white");
            $("#lng1").css("background-color","white");
            $("#lat2").css("background-color","yellow");
            $("#lng2").css("background-color","yellow");
        } else {
            document.getElementById("lng1").value = evt.coordinate[0].toFixed(6);
            document.getElementById("lat1").value = evt.coordinate[1].toFixed(6);
            $("#lat2").css("background-color","white");
            $("#lng2").css("background-color","white");
            $("#lat1").css("background-color","yellow");
            $("#lng1").css("background-color","yellow");
        }
        count = count + 1;
    }
});

//画直线
/* 自定义工具 */
let draw1, select, modify;
let lflag = 0;
$('#drawline').click(function () {
    map.removeInteraction(lineDraw);
    clickflag=1;
    map.removeInteraction(draw1); //点击选择时候  取消绘图交互
    draw1 = new ol.interaction.Draw({
        source: source1,
        type: 'LineString',
        maxPoints: 2
    });
    draw1.on('drawstart', function(){
        source1.clear();
    });
    if (lflag=0){
        map.addLayer()
    }
    map.addInteraction(draw1); //增加的交互
});
//结束绘制按钮
let endDrawFBtn = document.getElementById('endDrawFBtn');
endDrawFBtn.onclick = function () {
    clickflag=0;
    map.removeInteraction(draw1); //点击选择时候  取消绘图交互
    $("#lat2").css("background-color","white");
    $("#lng2").css("background-color","white");
    $("#lat1").css("background-color","white");
    $("#lng1").css("background-color","white");
};

//
let geometryfunction;
geometryfunction = function(coordinates, geometry){
    if(!geometry){
        geometry = new ol.geom.Polygon(null);       //多边形
    }
    let start = coordinates[0];
    let end = coordinates[1];
    geometry.setCoordinates([
        [
            start,
            [start[0], end[1]],
            end,
            [end[0], start[1]],
            start
        ]
    ]);
    return geometry;
};
let lineDraw;
//添加交互----绘制截面feature
function addInteractions(source) {
    clickflag=1;
    lineDraw = new ol.interaction.Draw({
        type: 'LineString',
        geometryFunction: geometryfunction,
        source: source,    // 注意设置source，这样绘制好的线，就会添加到这个source里
        maxPoints: 2
    });
    lineDraw.on('drawstart', function(){
        source1.clear();
    });
    // 监听线绘制结束事件，获取坐标DRAWSTART
    // lineDraw.on('drawend', function(event){
    //     // event.feature 就是当前绘制完成的线的Feature
    //     // document.getElementById('areaData').innerHTML = JSON.stringify(event.feature.getGeometry().getCoordinates());
    // });
    map.addInteraction(lineDraw);
}

//开始绘制截面按钮
let starDrawFBtn = document.getElementById('starDrawFBtn');
starDrawFBtn.onclick = function() {
    map.removeInteraction(draw1);
    source1.clear();
    addInteractions(source1);

};

$('#endDraw').click(function () {
    clickflag=0;
    map.removeInteraction(lineDraw); //点击选择时候  取消绘图交互
    $("#lat2").css("background-color","white");
    $("#lng2").css("background-color","white");
    $("#lat1").css("background-color","white");
    $("#lng1").css("background-color","white");
})