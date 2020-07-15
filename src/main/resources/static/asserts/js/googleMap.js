
// // 实例?化鼠标位置控件
// const mousePositionControl = new ol.control.MousePosition({
//     coordinateFormat: function (coord) {
//         return ol.coordinate.toStringHDMS(coord);
//     }, //坐标格式
//     projection: 'EPSG:4326', //地图投影坐标系
//     className: 'custom-mouse-position', //坐标信息显示样式
//     // 显示鼠标位置信息的目标容器
//     target: document.getElementById('mouse-position'),
//     undefinedHTML: '&nbsp' //未定义坐标的标记
// });
//画断面图层
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

// 计算使用的分辨率
// for(var i=0; i<=maxZoom; i++){
//     resolutions[i] = Math.pow(2, maxZoom-i);
// }
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

// google地图层
var googleMapLayer = new ol.layer.Tile({
    source: new ol.source.XYZ({
        url:'http://www.google.cn/maps/vt/pb=!1m4!1m3!1i{z}!2i{x}!3i{y}!2m3!1e0!2sm!3i345013117!3m8!2szh-CN!3scn!5e1105!12m4!1e68!2m2!1sset!2sRoadmap!4e0'
    })
});
const map = new ol.Map({
    target: 'map',
    layers: [googleMapLayer
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

let option = {
    animation: false,
    backgroundColor: 'transparent',
    title: {
        text: '船舶轨迹点',
        left: 'center',
        top: 'top',
        textStyle: {
            color: '#ff527d'
        }
    },
    // tooltip: {},
    legend: {
        left: 'left',
        data: ['轨迹点', '中'],
        textStyle: {
            color: '#cc152b'
        }
    },
    series: [
        {
            name: '轨迹点',
            type: 'scatter',
            symbolSize: 3,
            itemStyle: {
                shadowBlur: 3,
                shadowColor: 'rgb(0,170,23)',
                color: 'rgb(0,170,23)',
                opacity: 0.8,
            },
            // itemStyle: {
            //     color: 'rgb(162,255,79)'
            // },
            postEffect: {
                enable: true
            },
            // silent: true, //高亮
            // blendMode: 'lighter',
            large: true,
            dimensions: ['lng', 'lat'],
            progressive: 1e6,
            progressiveThreshold: 1000 + Math.random(),
            data: new Float32Array()
        },{
            name: '中',
            type: 'scatter',
            symbolSize: 3,
            itemStyle: {
                shadowBlur: 2,
                shadowColor: 'rgba(204,21,43,0.8)',
                color: 'rgba(204,21,43,0.8)'
            },
            large: true,
            dimensions: ['lng', 'lat'],
            progressive: 1e6,
            progressiveThreshold: 1000 + Math.random(),
            data:new Float32Array()
        }
    ]
};
let echartslayer = new ol3Echarts(option, {
    forcedRerender: false, // 强制重绘，会调用 ECharts 的 clear() 方法清空图层
    forcedPrecomposeRerender: false, // 强制在 map 触发 precompose（准备渲染，未开始渲染）事件时进行 ECharts 图层的重绘
    hideOnZooming: false, // 在地图缩放时隐藏 ECharts 图层，这样在一些场景下可以提高性能。
    hideOnMoving: false, // 在地图移动时隐藏 ECharts 图层，这样在一些场景下可以提高性能。
    hideOnRotating: false, // 在地图旋转时隐藏 ECharts 图层，这样在一些场景下可以提高性能。
    opacity: 0.5,
});
echartslayer.appendTo(map);