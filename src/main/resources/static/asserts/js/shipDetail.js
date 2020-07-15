const map = new HMap('map', {
    controls: {
        loading: true,
        zoomSlider: true,
        fullScreen: false
    },
    view: {
        center: [113.53450137499999, 34.44104525],
        projection: 'EPSG:4326',
        zoom: 5, // resolution
    },
    baseLayers: [
        {
            layerName: 'vector',
            isDefault: true,
            layerType: 'TileXYZ',
            projection: 'EPSG:3857',
            tileGrid: {
                tileSize: 256,
                extent: [-2.0037507067161843E7, -3.0240971958386254E7, 2.0037507067161843E7, 3.0240971958386205E7],
                origin: [-2.0037508342787E7, 2.0037508342787E7],
                resolutions: [
                    156543.03392800014,
                    78271.51696399994,
                    39135.75848200009,
                    19567.87924099992,
                    9783.93962049996,
                    4891.96981024998,
                    2445.98490512499,
                    1222.992452562495,
                    611.4962262813797,
                    305.74811314055756,
                    152.87405657041106,
                    76.43702828507324,
                    38.21851414253662,
                    19.10925707126831,
                    9.554628535634155,
                    4.77731426794937,
                    2.388657133974685
                ]
            },
            layerUrl: 'http://cache1.arcgisonline.cn/arcgis/rest/services/ChinaOnlineStreetPurplishBlue/MapServer/tile/{z}/{y}/{x}'
        }
    ]
});
const echartslayer = new ol3Echarts(null);
echartslayer.appendTo(map.getMap());
let myChart3 = echarts.init(document.getElementById('speeds'));
let option3 = {
    //鼠标悬浮提示框
    tooltip: {
        trigger: 'axis',
        axisPointer: {
            type: 'cross',
            animation: false
        }
    },
    //标题
    title: {
        text: '航行路程图（速度-时间）',
        left: 'center',
        top: 16
    },
    toolbox: {
        feature: {
            dataZoom: {
                yAxisIndex: 'none'
            },
            restore: {},
            saveAsImage: {}
        }
    },
    //图表标识
    legend: {
        data: ['航速'],
        left: 10
    },
    xAxis: {
        type: 'category',
        boundaryGap: false,
        data: [],
        name: '时间'
    },
    yAxis: {
        type: 'value',
        min: 0,
        name: '航速(节)'
    },
    dataZoom: [
        {   // 这个dataZoom组件，默认控制x轴。
            type: 'slider', // 这个 dataZoom 组件是 slider 型 dataZoom 组件
            start: 30,      // 左边在 10% 的位置。
            end: 70         // 右边在 60% 的位置。
        },
        {   // 这个dataZoom组件，也控制x轴。
            type: 'inside', // 这个 dataZoom 组件是 inside 型 dataZoom 组件
            start: 30,      // 左边在 10% 的位置。
            end: 70         // 右边在 60% 的位置。
        }
    ],
    series: [{
        data: [],
        type: 'line',
        areaStyle: {},
        markPoint: {
            data: [
                {type: 'max', name: '最大值'},
                {type: 'min', name: '最小值'}
            ]
        },
        markLine: {
            data: [
                {type: 'average', name: '平均值'}
            ]
        }
    }]
};
myChart3.setOption(option3);
let ship = sessionStorage.getItem("shipdetail");
console.log(ship)
let mmsi = sessionStorage.getItem("ship_mmsi");
let shipname = sessionStorage.getItem("ship_name");
let tmp;


// let html1 = "";
//
//     for (let i1 = 0; i1 < list.shiptype.length; i1++) {
//         html1 += "<tr><td>" + list.shiptype[i1] + "</td><td>"
//             + list.shipnum[i1] + "</td></tr>";
//     }
//     $('#shiptype_content').html(html1);
//

$(function () {
    $(document).ready(function () {
        


        $.ajax({
            type: "POST",
            url: "/ajax/queryShip",
            data: {
                mmsi: mmsi,
                shipname: shipname
            },
            dataType: "json",
            contentType: 'application/json',
            beforeSend: function () {
                document.getElementById("select_mmsi").innerText="加载中";
            },
            success: function (list) {
                document.getElementById("select_mmsi").innerText=list.get(0).getMmsi();

                console.log(list)
            }
        })
    })
});