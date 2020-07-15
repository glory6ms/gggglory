let data = JSON.parse(sessionStorage.getItem('trackList'));
let myChart2 = echarts.init(document.getElementById('shiptrack'));
let option2 = {
    tooltip: {
        trigger: 'axis',
        axisPointer: {
            type: 'cross'
        }
    },
    title: {
        text: '轨迹点经纬度路径图',
        left: 'center',
        top: 16
    },
    xAxis: {
        type: 'category',
        data: [],
        name: '经度'
    },
    yAxis: {
        type: 'category',
        name: '纬度',
        splitLine: {
            lineStyle: {
                type: 'dashed'
            }
        }
    },
    series: [{
        data: [],
        type: 'line',
        symbol: 'triangle',
        symbolSize: 20,
        lineStyle: {
            color: 'green',
            width: 4,
            type: 'dashed'
        },
        itemStyle: {
            borderWidth: 3,
            borderColor: 'yellow',
            color: 'blue'
        }
    }]
};
myChart2.setOption(option2);

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
        text: '速度分布图（速度-空间）',
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

let myChart4 = echarts.init(document.getElementById('land'));
let option4 = {
    tooltip: {
        trigger: 'axis',
        axisPointer: {
            type: 'cross'
        }
    },
    title: {
        text: '船舶时空图',
        subtext: '线性拟合单位时间段内行驶的距离',
        left: 'center',
        top: 16
    },
    xAxis: {
        type: 'value',
        splitLine: {
            lineStyle: {
                type: 'dashed'
            }
        },
        splitNumber: 20,
        name: '时间段(t)'
    },
    yAxis: {
        type: 'value',
        min: 0,
        splitLine: {
            lineStyle: {
                type: 'dashed'
            }
        },
        splitNumber: 6,
        name: '航行距离（海里）'
    },
    grid: {
        top: 90
    },
    dataZoom: [{
        show: true,
        type: 'inside',
        filterMode: 'none',
        xAxisIndex: [0],
        startValue: 0,
        endValue: 90
    }, {
        show: true,
        type: 'inside',
        filterMode: 'none',
        yAxisIndex: [0],
        startValue: 0,
        endValue: 80
    }],
    series: [{
        name: 'scatter',
        type: 'scatter',
        emphasis: {
            label: {
                show: true,
                position: 'right',
                color: 'blue',
                fontSize: 16
            }
        },
        data: []
    }, {
        name: 'line',
        type: 'line',
        smooth: true,
        showSymbol: false,
        data: [],
        markPoint: {
            itemStyle: {
                color: 'transparent'
            },
            label: {
                show: true,
                position: 'left',
                formatter: [],
                color: '#333',
                fontSize: 14
            },
            data: [{
                coord: []
            }]
        }
    }]
};

myChart4.setOption(option4);


let maps;
$(function () {
    $(document).ready(function () {
        let timein = sessionStorage.getItem('timein');
        let timeout = sessionStorage.getItem('timeout');

        let period = {
            timein: timein,
            timeout: timeout
        };
        // console.log(JSON.parse(list)[0].mmsi);
        let area = {
            collection:data,
            period:period
        };

        $.ajax({
            type: "POST",
            url: "/ajax/analysis/queryByMMSI",
            data: JSON.stringify(area),
            dataType: "json",
            contentType: 'application/json',
            beforeSend: function () {
                document.getElementById("select_mmsi").innerText="加载中";
            },
            success: function (list) {
                document.getElementById("select_mmsi").innerText="请选择mmsi"
                maps=list;
                $('#mmsi').trigger('change');
                //统计静态信息
                let optionstring = "";
                //多选框的值为下标
                for (let t = 0; t < list.mmsi.length; t++) {
                    optionstring += "<option value=\"" + t + "\" > " + list.mmsi[t] + "</option>";
                }
                $('#mmsi').html(optionstring);
                let seriesData = [];
                let selected = {};
                if(list.shiptype!="-1"){
                    for (let s=0;s<list.shiptype.length;s++){
                        seriesData.push({
                            name: list.shiptype[s],
                            value: list.shipnum[s]
                        });
                        if (list.shipnum>0){
                            selected[list.shiptype[s]]=1;
                        }
                    }
                    let myChart5 = echarts.init(document.getElementById('shiptype'),'macarons');
                    let option5 = {
                        title: {
                            text: '船舶类型统计',
                            subtext: '各类船舶数量及占比',
                            left: 'center'
                        },
                        tooltip: {
                            trigger: 'item',
                            formatter: '{a} <br/>{b} : {c} ({d}%)'
                        },
                        legend: {
                            type: 'scroll',
                            orient: 'vertical',
                            right: 10,
                            top: 20,
                            bottom: 20,
                            data: list.shiptype,
                            selected: selected
                        },
                        series: [
                            {
                                name: '船舶类型',
                                type: 'pie',
                                radius: '55%',
                                center: ['40%', '50%'],
                                data: seriesData,
                                emphasis: {
                                    itemStyle: {
                                        shadowBlur: 10,
                                        shadowOffsetX: 0,
                                        shadowColor: 'rgba(0, 0, 0, 0.5)'
                                    }
                                }
                            }
                        ]
                    };
                    myChart5.setOption(option5);
                }else {
                    $("#shiptype").html("数据不全，查询不到静态信息,无法统计船舶种类");
                }
                /**
                 * 1.船舶类型,直接到前端页面循环
                 * 2.吃水
                 * 3.船长0-50,50-100,100-150,150>
                 * lists中key为staticdata是所以查出来的静态信息
                 * 其他都是船舶类型
                 */

                let html1 = "";


                if (list.shiptype!="-1"){
                    for (let i1 = 0; i1 < list.shiptype.length; i1++) {
                        html1 += "<tr><td>" + list.shiptype[i1] + "</td><td>"
                            + list.shipnum[i1] + "</td></tr>";
                    }
                    $('#shiptype_content').html(html1);
                }else {
                    $('#shiptype_content').html("数据不全，查询不到静态信息");
                }
            }
        })
    })
});
let speeds = [0, 0, 0, 0, 0, 0, 0];
for (let i = 0; i < data.length; i++) {
    if (data[i].landSpeed<2){

    } else if (data[i].landSpeed < 4) {
        speeds[0]++;
    } else if (data[i].landSpeed < 8) {
        speeds[1]++;
    } else if (data[i].landSpeed < 12) {
        speeds[2]++
    } else if (data[i].landSpeed < 16) {
        speeds[3]++;
    } else if (data[i].landSpeed< 20){
        speeds[4]++;
    }else {
        speeds[5]++;
    }
}
let myChart1 = echarts.init(document.getElementById('shipspeed'),'macarons');
let option1 = {
    title: {
        text: '断面分析',
        subtext: '对地速度统计'
    },
    tooltip: {
        trigger: 'axis'
    },
    // legend: {
    //     data: ['对地航速', '对地航速']
    // },
    toolbox: {
        show: true,
        feature: {
            dataView: {show: true, readOnly: false},
            magicType: {show: true, type: ['line', 'bar']},
            restore: {show: true},
            saveAsImage: {show: true}
        }
    },
    calculable: true,
    xAxis: [
        {
            type: 'category',
            data: ['0-4(节)', '4-8(节)', '8-12(节)', '12-16(节)', '16-20(节)' , '>20(节)']
        }
    ],
    yAxis: [
        {
            type: 'value'
        }
    ],
    series: [
        {
            name: '船舶数量',
            type: 'bar',
            data: [speeds[0], speeds[1], speeds[2], speeds[3], speeds[4], speeds[5]],
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
        },
        {
            name: '船舶数量',
            type: 'line',
            data: [speeds[0], speeds[1], speeds[2], speeds[3], speeds[4], speeds[5]]
        }
    ]
};
// 使用刚指定的配置项和数据显示图表。
myChart1.setOption(option1);

let state = new Array();
let state_num = new Array();
let state_data= new Array();
let s = 0,sflag=0;
for(let i=0;i<data.length;i++){
    if (state.length!=0){

        for (let e=0;e<state.length;e++){
            if (state[e]==data[i].shipState) {
                sflag=1;
                break;
            }
        }
    }
    if (sflag==0){
        state[s] = data[i].shipState;
        state_num[s] = 1;
        for (let j = i+1;j<data.length;j++){
            if (data[j].shipState==state[s]) {
                state_num[s]+=1;
            }
        }
        state_data[s]= {value: state_num[s], name: state[s]};
        s=s+1;
    }

}
console.log(state_data);

let myChart6 = echarts.init(document.getElementById('shipstate'));
let option6 = {
    backgroundColor: '#2c343c',

    title: {
        text: 'Customized Pie',
        left: 'center',
        top: 20,
        textStyle: {
            color: '#ccc'
        }
    },

    tooltip: {
        trigger: 'item',
        formatter: '{a} <br/>{b} : {c} ({d}%)'
    },

    visualMap: {
        show: false,
        min: 0,
        max: 200,
        inRange: {
            colorLightness: [0.3, 1],
            // symbolSize: [30, 100]
        },
        outOfRange:{  //定义 在选中范围外 的视觉元素。
            color: ['#121122', 'rgba(3,4,5,0.4)', 'red'],
            symbolSize: [30, 1000]
        },
    },
    series: [
        {
            name: '访问来源',
            type: 'pie',
            radius: '55%',
            center: ['50%', '50%'],
            data: state_data.sort(function (a, b) { return a.value - b.value; }),
            roseType: 'radius',
            label: {
                color: 'rgba(255, 255, 255, 0.3)'
            },
            labelLine: {
                lineStyle: {
                    color: 'rgba(255, 255, 255, 0.3)'
                },
                smooth: 0.2,
                length: 10,
                length2: 20
            },
            itemStyle: {
                color: '#c23531',
                shadowBlur: 200,
                shadowColor: 'rgba(0, 0, 0, 0.5)'
            },
            animationType: 'scale',
            animationEasing: 'elasticOut',
            animationDelay: function (idx) {
                return Math.random() * 200;
            }
        }
    ]
};
myChart6.setOption(option6);


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


let val=0;
$("#mmsi").change(function () {
    if($(this).val()!=null){
        val = $(this).val();
    }
    let track = maps.area;;
    let lng = new Array();
    let lat = new Array();
    let speed = new Array();
    let time = new Array();
    let land = new Array();
    let zland = new Array();
    let moveLines = new Array();
    for (let l = 0; l < track[val].collection.length; l++) {
        lng[l] = track[val].collection[l].lng;
        lat[l] = track[val].collection[l].lat;
        speed[l] = track[val].collection[l].landSpeed;
        time[l] = track[val].collection[l].time.substring(0, 10).concat(track[val].collection[l].time.substring(11, 18));

        // moveLines[l][0] =
    }
    for (let y = 1; y < track[val].collection.length; y++) {
        land[y] = Math.sqrt((lng[y] - lng[y-1]) * (lng[y] - lng[y-1]) + (lat[y] - lat[y-1]) * (lat[y] - lat[y-1]));
        //转换为海里
        land[y] = Math.abs(land[y]) * 60;
    }

    //总里程数和增加的里程数
    land[0] = 0;
    //异步刷新charts
    myChart2.setOption({
        xAxis: {
            data: lng
        },
        series: [{
            // 根据名字对应到相应的系列
            data: lat,
        }]
    });
    myChart3.setOption({
        xAxis: {
            name: '时间',
            data: time
        },
        series: [{
            // 根据名字对应到相应的系列
            name: '航速',
            data: speed
        }]
    });
    for (let i = 0; i < track[val].collection.length; i++) {
        zland[i] = new Array(2);
        zland[i][0] = i;
        zland[i][1] = Number(land[i]).toFixed(5);

        if (i<track[val].collection.length-1) {
        let lag =[lng[i],lat[i]];
        let lag2 =[lng[i+1],lat[i+1]];

        moveLines[i] = new Array(2);
        moveLines[i][0]=lag;
        moveLines[i][1]=lag2;
        }
    }
    let myRegression = ecStat.regression('polynomial', zland, 3);
    myRegression.points.sort(function (a, b) {
        return a[0] - b[0];
    });
    myChart4.setOption({

        series: [{
            // 根据名字对应到相应的系列
            name: 'scatter',
            data: zland
        }, {
            name: 'line',
            data: myRegression.points,
            markPoint: {
                label: {
                    formatter: myRegression.expression,
                },
                data: [{
                    coord: myRegression.points[myRegression.points.length - 1]
                }]
            }
        }

        ]
    });


    //轨迹回放

    var option8 = {
        backgroundColor: 'transparent',
        title: {
            text: '轨迹回放',
            left: 'center',
            textStyle: {
                color: '#fff'
            }
        },
        legend: {
            show: false,
            orient: 'vertical',
            top: 'top',
            left: 'right',
            data: ['航迹'],
            textStyle: {
                color: '#fff'
            }
        },
        series: [
            {
                name: '航迹',
                type: 'lines',
                large: true,
                zlevel: 2,
                effect: {
                    show: true,
                    constantSpeed: 30,
                    symbol: 'pin',
                    symbolSize: 3,
                    trailLength: 0,
                },
                lineStyle: {
                    normal: {
                        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [{
                            offset: 0, color: '#16cc0c'
                        }, {
                            offset: 1, color: '#16cc0c'
                        }], false),
                        width: 1,
                        opacity: 0.2,
                        curveness: 0.1
                    }
                },
                data: moveLines
            }
        ]
    };

    map.getView().setCenter([lng[1],lat[1]])
    map.getView().setZoom(12)
    echartslayer.setChartOptions(option8);
})

