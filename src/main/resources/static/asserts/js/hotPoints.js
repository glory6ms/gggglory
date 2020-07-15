
function showQuery() {
    document.getElementById('rightcard').style.display = 'block';
}

function hid(type) {
    if (type==1){
        document.getElementById('rightcard').style.display = 'none';
    } else {
        document.getElementById('draggable1').style.display = 'none';
    }

}

$(function () {
    $('.datepicker').datepicker({
        todayHighlight: true,
        orientation: "bottom left",
        format: 'yyyy-mm-dd',
        autoclose: true,
        language: 'zh-CN',
        startDate: '2017-10-01',
        endDate: '2017-10-31'
    });
});
$(function(){
    $('#draggable1').draggabilly();
});

//ajax查询--按时间查询ajaxQuery
let page=0
let re ;
let re1 ;
let jflag=0,kflag=0,flag= 0;

$(function () {
    $('#ajaxQuery').click(function hotpoint(next) {
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
        next=page;
        $.ajax({
            // url: "/ajax/queryByTime",
            url:"/ajax/dbscan",
            type: "POST",
            data: {
                timein: timein,
                timeout: timeout,
                next:next
            },
            dataType: "json",
            beforeSend: function () {
                $("#ajaxQuery").text("查询中..");

                document.getElementById('card_body').style.display = 'none';
                document.getElementById('spinner').style.display = 'block';
            },
            success: function (list) {
                $("#ajaxQuery").text("船舶查询");
                if (list.length == 0) {
                    alert("此时间段查询不到数据");
                    return;
                }
                //设置显示区域
                const ext = [0, 0, 200, 200];
                const ret = new Float32Array(list.length * 2);
                const ret1 = new Float32Array(list.length * 2);
                let j=0,k=0
                //总页数
                let num=list[0].num;
                //总条数
                let total =0;
                total=list[1].num;
                if (flag==0){
                    re  = new Float32Array(total);
                    re1 = new Float32Array(total);
                    flag=1;
                }
                list.forEach(function (v) {
                    if (!v.isNoised){
                        if (v.cluster<1){
                            re[jflag*2]=ret[j * 2] = v.x
                            re[jflag*2+1]=ret[1 + (j * 2)] = v.y
                            j = j + 1;
                            jflag+=1;
                        }else{
                            re1[kflag*2]=ret1[k*2] = v.x
                            re1[kflag*2+1]=ret1[k*2+1] = v.y
                            k=k+1;
                            kflag+=1;
                        }
                    }
                })

                //设置数据格式
                echartslayer.appendData({
                    seriesIndex: 0,
                    data: ret
                }, true);
                echartslayer.appendData({
                    seriesIndex: 1,
                    data: ret1
                }, true)
                $('#tip').text("禁止地图缩放与平移")
                page+=1;
                if (page<num){
                    hotpoint(page)
                }else {
                    setTimeout(showSpinner, 2000);
                    echartslayer.appendData({
                        seriesIndex: 0,
                        data: re
                    });
                    echartslayer.appendData({
                        seriesIndex: 1,
                        data: re1
                    })
                    page=0;
                }
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