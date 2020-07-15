//ajax查询--按时间段查询ajaxQuery
$(function () {
    $('#ajaxQuery').click(function () {
        //获取起止日期
        let timein;
        let timeout;
        timein = $("#timein").val();
        timeout = $("#timeout").val();
        if (timein == "" || timeout == "") {
            alert("请填写查询时间段");
            return;
        }
        if (timeout <= timein) {
            $("#titlemessage").text("结束时间不能小于开始时间！");
            return;
        }
        let data = {};
        data['timein'] = timein;
        data['timeout'] = timeout;
        sessionStorage.setItem("timein", timein);
        sessionStorage.setItem("timeout", timeout);
        $.ajax({
            // url: "/ajax/queryByTime",
            url:"/ajax/queryByTime",
            type: "POST",
            data: {
                timein: timein,
                timeout: timeout
            },
            dataType: "json",
            beforeSend: function () {
                $("#ajaxQuery").text("查询中..");
                document.getElementById('card_body').style.display = 'none';
                document.getElementById('spinner').style.display = 'block';
            },
            success: function (list) {
                console.log("船舶轨迹点数量"+list.length)
                $("#ajaxQuery").text("船舶查询");
                if (list.length == 0) {
                    alert("此时间段查询不到数据");
                    return;
                }
                //设置显示区域
                const ext = [0, 0, 200, 200];
                const ret = new Float32Array(list.length * 2);
                let j=0
                list.forEach(function (v) {
                            ret[j * 2] = v.lng
                            ret[1 + (j * 2)] = v.lat
                            j = j + 1;
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
                        data: ['轨迹点'],
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
                            silent: true, //高亮
                            blendMode: 'lighter',
                            large: true,
                            dimensions: ['lng', 'lat'],
                            progressive: 1e6,
                            progressiveThreshold: 1000 + Math.random(),
                            data: new Float32Array()
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
                //设置数据格式
                echartslayer.appendData({
                    seriesIndex: 0,
                    data: ret
                });
                $('#tip').text("禁止地图缩放与平移")
                // setTimeout(showSpinner, 2000);
                showSpinner();
            },
            error: function () {
                alert("error,请先填写所查询的时间段");
                $("#ajaxQuery").text("船舶查询");
            }
        })
    })

})


//按断面查询ajax
$(function () {
    $('#SectionSearch').click(
        function () {
            let timein = sessionStorage.getItem('timein');
            let timeout = sessionStorage.getItem('timeout');

            let lng1 = $("#lng1").val();
            let lng2 = $("#lng2").val();
            let lat1 = $("#lat1").val();
            let lat2 = $("#lat2").val();
            if (lng1 == "" || timein == null) {
                alert("请填写查询时间段并绘制断面");
                return;
            }
            let period = {
                timein: timein,
                timeout: timeout
            };
            // console.log(JSON.parse(list)[0].mmsi);
            let area = {

                lng1: lng1,
                lat1: lat1,
                lng2: lng2,
                lat2: lat2,
                period: period
            };
            $.ajax({
                type: "POST",
                url: "/ajax/sectionsearch",
                data: JSON.stringify(area),
                dataType: "json",
                contentType: 'application/json',
                beforeSend: function () {

                    document.getElementById('card_body').style.display = 'none';
                    document.getElementById('spinner').style.display = 'block';
                },
                success: function (data1) {
                    let data = data1.dlist;
                    let html1 = "";
                    let html2 = "";
                    let html3 = "";
                    // data.forEach(function (e) {
                    //     // html += "<tr><td>" + e.mmsi + "</td><td>"
                    //     //     + e.lng.toFixed(6) + "</td><td>"
                    //     //     + e.lat.toFixed(6) + "</td><td>"
                    //     //     + e.landSpeed + "</td><td>"
                    //     //     + e.landCourse + "</td></tr>";
                    //
                    //     delete e.did
                    //     delete e.precion
                    //     delete e.shipLength
                    //     delete e.shipWidth
                    //     delete e.aisType
                    //     delete e.shipCourse
                    //     delete e.shipName
                    //     delete e.turnRate
                    //     delete e.shipType
                    //     delete e.time
                    // })
                    let speeds = [0, 0, 0, 0, 0, 0];
                    for (let i = 0; i < data.length; i++) {
                        if(data[i].landSpeed<2){

                        }else if (data[i].landSpeed < 3) {
                            speeds[0]++;
                        } else if (data[i].landSpeed < 6) {
                            speeds[1]++;
                        } else if (data[i].landSpeed < 9) {
                            speeds[2]++
                        } else if (data[i].landSpeed < 12) {
                            speeds[3]++;
                        } else {
                            speeds[4]++;
                        }
                        delete data[i].did
                        delete data[i].precion
                        delete data[i].shipLength
                        delete data[i].shipWidth
                        delete data[i].aisType
                        delete data[i].shipCourse
                        delete data[i].shipName
                        delete data[i].turnRate
                        delete data[i].shipType
                        delete data[i].time

                    }
                    if (data1.shiplength != -1) {
                        html1 = "<tr><td> < 30 m</td><td>" + data1.shiplength[0] + "</td></tr>"
                            + "<tr><td>30-60 m</td><td>" + data1.shiplength[1] + "</td></tr>"
                            + "<tr><td>60-90 m</td><td>" + data1.shiplength[2] + "</td></tr>"
                            + "<tr><td>90-120 m</td><td>" + data1.shiplength[3] + "</td></tr>"
                            + "<tr><td>120-150 m</td><td>" + data1.shiplength[4] + "</td></tr>"
                            + "<tr><td>>150 m</td><td>" + data1.shiplength[5] + "</td></tr>";

                        html2 = "<tr><td> < 3 m</td><td>" + data1.waterdepth[0] + "</td></tr>"
                            + "<tr><td>3-6 m</td><td>" + data1.waterdepth[1] + "</td></tr>"
                            + "<tr><td>6-9 m</td><td>" + data1.waterdepth[2] + "</td></tr>"
                            + "<tr><td>9-12 m</td><td>" + data1.waterdepth[3] + "</td></tr>"
                            + "<tr><td>>12 m</td><td>" + data1.waterdepth[4] + "</td></tr>";

                        $('#shipnum_length').html(html1)
                        $('#shipnum_water').html(html2)

                    } else {
                        $('#shipnum_length').html("数据不全")
                        $('#shipnum_water').html("数据不全")
                    }
                    // $('#content').html(html);
                    html3 = "<tr><td> 0-3(节)</td><td>" + speeds[0] + "</td></tr>"
                        + "<tr><td>3-6(节)</td><td>" + speeds[1] + "</td></tr>"
                        + "<tr><td>6-9(节)</td><td>" + speeds[2] + "</td></tr>"
                        + "<tr><td>9-12(节)</td><td>" + speeds[3] + "</td></tr>"
                        + "<tr><td>大于12(节)</td><td>" + speeds[4] + "</td></tr>";

                    $('#shipspeed_content').html(html3)
                    $('#flow_data').text('流量:' + data.length)
                    // $('#myModal').modal('show');
                    document.getElementById('draggable1').style.display = 'block'
                    sessionStorage.setItem('trackList', JSON.stringify(data));
                    setTimeout(showSpinner, 1000);
                },
                error: function () {
                    alert("error");
                }
            })
        });
});
//截面查询
$(function () {
    $('#Section').click(
        function () {

            let timein = sessionStorage.getItem('timein');
            let timeout = sessionStorage.getItem('timeout');

            let list1 = $('#lat2').val();
            if (list1 == "" || timein == null) {
                alert("请填写查询时间段并绘制截面");
                return;
            }
            // let dynamiclist = JSON.parse(list);
            let lng1 = $("#lng1").val();
            let lng2 = $("#lng2").val();
            let lat1 = $("#lat1").val();
            let lat2 = $("#lat2").val();
            let period = {
                timein: timein,
                timeout: timeout
            };
            // console.log(JSON.parse(list)[0].mmsi);
            let area = {
                // collection: dynamiclist,
                lng1: lng1,
                lat1: lat1,
                lng2: lng2,
                lat2: lat2,
                period: period
            };
            $.ajax({
                type: "POST",
                url: "/ajax/section",
                data: JSON.stringify(area),
                dataType: "json",
                contentType: 'application/json',
                beforeSend: function () {

                    document.getElementById('card_body').style.display = 'none';
                    document.getElementById('spinner').style.display = 'block';
                },
                success: function (data1) {
                    let data = data1.dlist;
                    let html1 = "";
                    let html2 = "";
                    let html3 = "";
                    if (data1.shiplength != -1) {
                        html1 = "<tr><td> < 30 m</td><td>" + data1.shiplength[0] + "</td></tr>"
                            + "<tr><td>30-60 m</td><td>" + data1.shiplength[1] + "</td></tr>"
                            + "<tr><td>60-90 m</td><td>" + data1.shiplength[2] + "</td></tr>"
                            + "<tr><td>90-120 m</td><td>" + data1.shiplength[3] + "</td></tr>"
                            + "<tr><td>120-150 m</td><td>" + data1.shiplength[4] + "</td></tr>"
                            + "<tr><td>>150 m</td><td>" + data1.shiplength[5] + "</td></tr>";

                        html2 = "<tr><td> < 3 m</td><td>" + data1.waterdepth[0] + "</td></tr>"
                            + "<tr><td>3-6 m</td><td>" + data1.waterdepth[1] + "</td></tr>"
                            + "<tr><td>6-9 m</td><td>" + data1.waterdepth[2] + "</td></tr>"
                            + "<tr><td>9-12 m</td><td>" + data1.waterdepth[3] + "</td></tr>"
                            + "<tr><td>>12 m</td><td>" + data1.waterdepth[4] + "</td></tr>";
                        $('#shipnum_length').html(html1)
                        $('#shipnum_water').html(html2)

                    } else {
                        $('#shipnum_length').html("数据不全")
                        $('#shipnum_water').html("数据不全")
                    }
                    let speeds = [0, 0, 0, 0, 0, 0];
                    for (let i = 0; i < data.length; i++) {
                        if (data[i].landSpeed < 3) {
                            speeds[0]++;
                        } else if (data[i].landSpeed < 6) {
                            speeds[1]++;
                        } else if (data[i].landSpeed < 9) {
                            speeds[2]++
                        } else if (data[i].landSpeed < 12) {
                            speeds[3]++;
                        } else {
                            speeds[4]++;
                        }
                    }
                    html3 = "<tr><td> 0-3(节)</td><td>" + speeds[0] + "</td></tr>"
                        + "<tr><td>3-6(节)</td><td>" + speeds[1] + "</td></tr>"
                        + "<tr><td>6-9(节)</td><td>" + speeds[2] + "</td></tr>"
                        + "<tr><td>9-12(节)</td><td>" + speeds[3] + "</td></tr>"
                        + "<tr><td>大于12(节)</td><td>" + speeds[4] + "</td></tr>";
                    $('#shipspeed_content').html(html3)
                    $('#flow_data').text('流量:' + data.length)
                    // $('#myModal').modal('show');
                    document.getElementById('draggable1').style.display = 'block'
                    sessionStorage.setItem('trackList', JSON.stringify(data));
                    setTimeout(showSpinner, 1000);
                },
                error: function () {
                    alert("error");
                }
            })
        });
});

function showSpinner() {
    document.getElementById('card_body').style.display = 'block';
    document.getElementById('spinner').style.display = 'none';
}