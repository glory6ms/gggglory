let myChart1 = echarts.init(document.getElementById('lngdis'));
let option1 = {
    tooltip: {
        trigger: 'axis',
        axisPointer: {
            type: 'cross'
        }
    },
    title: {
        text: '站点所处经度分布',
        left: 'center',
        top: 16
    },
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
    xAxis: {
        type: 'category',
        data: ['<70', '70-85', '85-100', '100-115', '115-130' , '>130'],
        name: '经度E'
    },
    yAxis: {
        type: 'value',
        name: '站点数'
    },
    series: [
        {
            name: '数量',
            type: 'bar',
            data: [],
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
            name: 'num',
            type: 'line',
            data: []
        }
    ]
};
myChart1.setOption(option1);


let myChart2 = echarts.init(document.getElementById('latdis'),'macarons');
let option2 = {
    tooltip: {
        trigger: 'axis',
        axisPointer: {
            type: 'cross'
        }
    },
    title: {
        text: '站点所处纬度分布',
        left: 'center',
        top: 16
    },
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
    xAxis: {
        type: 'category',
        data: ['<15', '15-25', '25-35', '35-45', '45-55' ],
        name: '纬度W'
    },
    yAxis: {
        type: 'value',
        name: '站点数'
    },
    series: [
        {
            name: '数量',
            type: 'bar',
            data: [],
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
            name: 'num',
            type: 'line',
            data: []
        }
    ]
};
myChart2.setOption(option2);
let maps;
$(function () {
    $(document).ready(function () {


        $.ajax({
            type: "GET",
            url: "/ajax/obs",
            success: function (list) {
                let min = 0, max = 0
                max = Math.max.apply(Math, list.map(function (o) {
                    return o.lng
                }))
                min = Math.min.apply(Math, list.map(function (o) {
                    return o.lng
                }))

                console.log(max+"++++++"+min)

                let l1=[0,0,0,0,0,0]
                let l2=[0,0,0,0,0]
                list.forEach(function (v) {
                   if (v.lng<70){
                        l1[0]+=1;
                   }else if (v.lng<85){
                       l1[1]+=1;
                   }else if(v.lng<100){
                       l1[2]+=1;
                   }else if(v.lng<115){
                       l1[3]+=1;
                   }else if(v.lng<130){
                       l1[4]+=1;
                   }else {
                       l1[5]+=1;
                   }

                    if (v.lat<15){
                        l2[0]+=1;
                    }else if (v.lat<25){
                        l2[1]+=1;
                    }else if(v.lat<35){
                        l2[2]+=1;
                    }else if(v.lat<45){
                        l2[3]+=1;
                    }else if(v.lat<55){
                        l2[4]+=1;
                    }
                    
                })

                myChart1.setOption({

                    series: [{
                        // 根据名字对应到相应的系列
                        name: '数量',
                        data: l1
                    },{
                        name: 'num',
                        data: l1
                    }]
                });

                myChart2.setOption({

                    series: [{
                        // 根据名字对应到相应的系列
                        name: '数量',
                        data: l2
                    },{
                        name: 'num',
                        data: l2
                    }]
                });

            }
        })
    })
});
