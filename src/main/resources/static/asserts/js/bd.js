$(function () {
    $('#draggable1').draggabilly({ handle: '.handle' });
});
var map = initMap(
    {
        style: whiteStyle,
        skyColors: [
            // 地面颜色
            'rgba(226, 237, 248, 0)',
            // 天空颜色
            'rgba(186, 211, 252, 1)'
        ]
    }
);
var point = new BMapGL.Point(110.74724, 15.27924);
map.centerAndZoom(point, 5);

var view = new mapvgl.View({
    // effects: [
    //     new mapvgl.BloomEffect({
    //         threshold: 0.8,
    //         // blurSize: 2
    //     }),
    // ],
    map: map
});

//热力图
var heatmap = new mapvgl.HeatmapLayer({
    size: 30000, // 单个点绘制大小
    max: 5, // 最大阈值
    unit: 'm',
    gradient: { // 对应比例渐变色
        0.1: 'rgba(90,190,223,1)',
        0.2: 'rgba(90,223,199,1)',
        0.3: 'rgba(90,223,112,1)',
        0.4: 'rgba(125,223,90,1)',
        0.5: 'rgba(183,223,90,1)',
        0.6: 'rgba(223,205,90,1)',
        0.7: 'rgba(223,176,90,1)',
        0.8: 'rgba(223,148,90,1)',
        0.9: 'rgba(223,90,90,1)',
        1: 'rgba(255, 0, 0, 1)'
    }
});
view.addLayer(heatmap);


var layer=null;

var polyline=null;

function draw(next) {
    $.ajax({
        url: "/ajax/draw",
        type: "POST",
        data: {
            next: next
        },
        dataType: "json",
        success: function (list) {
            heatmap.setData(null);
            let tx="_波高"+next;
            $('#bh').html(tx);
            var bddata = [];
            var max=0;
            list.forEach(function (v) {
                bddata.push({
                    geometry: {
                        type: 'Point',
                        coordinates: [v.lng, v.lat]
                    },
                    properties: {
                        count: v.shipName
                    }
                });
                if (v.shipName>max){
                    max=v.shipName
                }
            });
            max=max+2;

            heatmap.setData(bddata);
            draw_line(next)


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
            // if (polyline!=null){
            //     map.removeOverlay(polyline)
            // }
            if (layer!=null){
                view.removeLayer(layer);
            }
            // sessionStorage.setItem('route', JSON.stringify(list));
            let html1 = "";
            // var lines=[];
            var line_data=[];
            for(let i=1;i<list.length;i++){
                html1 += "<tr><td>" + i + "</td><td>"
                    + list[i][0] + "</td><td>"
                    + list[i][1] + "</td></tr>";
                // lines.push(new BMapGL.Point(list[i][0], list[i][1]))
                line_data.push([list[i][0], list[i][1]])
            }
            layer = new mapvgl.LineLayer({
                color: 'rgba(255, 71, 26, 0.8)',
                blend: 'lighter',
                width: 10,
                data: [{
                    geometry: {
                        type: 'LineString',
                        coordinates: line_data
                    }
                }]
            });
            // layer.setData(line);
            view.addLayer(layer);



            $('#route').html(html1);
            var p = new BMapGL.Point(list[list.length-1][0], list[list.length-1][1]-5);
            map.centerAndZoom(p, 6);

            // polyline = new BMapGL.Polyline(lines, {strokeColor:"red", strokeWeight:10, strokeOpacity:0.95});
            // map.addOverlay(polyline);

        },
        error: function () {
            alert("error");
        }
    })
}
