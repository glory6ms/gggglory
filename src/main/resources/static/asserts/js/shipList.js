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

let selectClick;
selectClick = new ol.interaction.Select({
    condition: ol.events.condition.click
});
map.addInteraction(selectClick);
selectClick.on('select', function (e) {
    let html = "";

    html += "<tr><td>" + e.selected[0].get("shipname") + "</td><td>"
        + e.selected[0].get("shiplength") + "</td><td>"
        + e.selected[0].get("waterdepth") + "</td><td>"
        + e.selected[0].get("shiptype") + "</td><td>"
        + e.selected[0].get("landspeed") + "</td><td>"
        + e.selected[0].get("destination") + "</td></tr>";

    $('#content').html(html);
    $('#myModal').modal('show');
});
$(function () {
    $('.datepicker').datepicker({
        todayHighlight: true,
        orientation: "bottom left",
        format: 'yyyy-mm-dd',
        autoclose: true,
        language: 'zh-CN',
        startDate: '2017-02-05',
        endDate: '2017-02-08'
    });
})
function showQuery() {
    document.getElementById('rightcard').style.display = 'block';
}

function hid() {
    document.getElementById('rightcard').style.display = 'none';
}

//ajax查询--按时间查询ajaxQuery
$(function () {
    $('#ajaxQuery').click(
        function () {
            let timein = $("#timein").val();
            let timeout = $("#timeout").val();
            if (timeout <= timein) {
                $("#titlemessage").text("结束时间不能小于开始时间！")
                return;
            }
            if (timein == "" || timeout == "") {
                alert("请填写查询时间段");
                return;
            }
            //此处可以判空、
            let data = {};
            data['timein'] = timein;
            data['timeout'] = timeout;
            $.ajax({
                url: "/ajax/static/queryByTime",
                type: "POST",
                data: {
                    timein: timein,
                    timeout: timeout
                },
                dataType: "json",
                beforeSend: function () {
                    $("#ajaxQuery").text("查询中..");
                },
                success: function (list) {
                    console.log("success");
                    //将按照时间查询出来结果放在浏览器储存内，方便其他模块公用
                    //   sessionStorage.setItem('dynamicList', JSON.stringify(list));
                    $("#ajaxQuery").text("船舶查询");
                    // let selectClick; //交互select
                    if (list.length == 0) {
                        alert("此时间段查询不到数据");
                        return;
                    }

                    let canvas = document.createElement('canvas');
                    canvas.width = 20;
                    canvas.height = 20;
                    let context = canvas.getContext("2d");
                    context.strokeStyle = "red";
                    context.lineWidth = 1;
                    context.beginPath();
                    context.moveTo(0, 0);
                    context.lineTo(10 , 5);
                    context.lineTo(0, 10);
                    context.lineTo(5, 5);
                    context.lineTo(0, 0);
                    context.stroke();
                    let symbols = [];
                    let k = 0;
                    let featureCount = list.length;
                    let features = new Array(featureCount);
                    let feature, geometry;
                    list.forEach(function (v) {
                        geometry = new ol.geom.Point([v[0], v[1]]);
                        feature = new ol.Feature(geometry);
                        symbols.push(new ol.style.Icon({
                            img: canvas,
                            imgSize: [canvas.width, canvas.height],
                            rotation: (v[3]-90) * Math.PI / 180,

                        }));
                        feature.setStyle(
                            new ol.style.Style({
                                image: symbols[k]
                            })
                        );
                        let key = ["lng", "lat", "landspeed", "landcourse", "mmsi", "shipname", "shiplength", "shipwidth", "waterdepth", "destination", "shiptype"];
                        for (let p = 0; p < 11; p++) {
                            feature.set(key[p], v[p]);
                        }
                        features[k] = feature;
                        k = k + 1;
                    });
                    const vectorSource = new ol.source.Vector({
                        features: features
                    });
                    const vector = new ol.layer.Vector({
                        source: vectorSource
                    });
                    map.addLayer(vector);
                    // 设置视图


                    map.getView().setCenter([list[6][0], list[6][1]]);
                    map.getView().setZoom(4);
                    naranja().success({
                        title: '查询成功',
                        text: '请点击图标查看船舶信息',
                    })
                    sessionStorage.setItem("shipdetail",list);
                },
                error: function () {
                    alert("error");
                }
            })
        });
});
