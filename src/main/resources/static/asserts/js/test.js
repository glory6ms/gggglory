var projection3 = ol.proj.get('EPSG:102100');
var tileUrl3 = "http://cache1.arcgisonline.cn/arcgis/rest/services/ChinaOnlineStreetPurplishBlue/MapServer/tile/{z}/{y}/{x}";
// 原点
var origin3 = [-2.0037508342787E7, 2.0037508342787E7];
// 分辨率
var resolutions3 = [
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
];
// 地图范围
var fullExtent3 = [-2.0037507067161843E7, -3.0240971958386254E7, 2.0037507067161843E7, 3.0240971958386205E7];
var tileGrid3 = new ol.tilegrid.TileGrid({
    tileSize: 256,
    origin: origin3,
    extent: fullExtent3,
    resolutions: resolutions3
});
// 瓦片数据源
var tileArcGISXYZ3 = new ol.source.XYZ({
    tileGrid: tileGrid3,
    projection: projection3,
    url: tileUrl3
});
const projection = ol.proj.get('EPSG:4326');

const origin = [-400, 400];
const resolutions = [0.023794610058302804, 0.00951784402332112,
    0.00475892201166056, 0.00237946100583028, 0.00118973050291514,
    0.00059486525145757, 0.000297432625728785,
    0.0001487163128643925, 0.00007435815643219625,
    0.000037179078216098126, 0.00001859072883855198,
    0.000009294174688773075, 0.000004647087344386537,
    0.00000237946100583028,
];

const fullExtent = [100.87588002846373, 16.45228966609097,
    136.40686460052386, 45.24664597260153
];

const map = new ol.Map({
    target: 'map',
    layers: [
        new ol.layer.Tile({
            source: tileArcGISXYZ3
        })
    ],
    view: new ol.View({
        center: [122, 29],
        projection: projection,
        extent: fullExtent,
        zoom: 3
    })
});
$(function () {
    $(document).ready(function () {
        $.ajax({
            url: "/ajax/queryTime",
            type: "GET",
            success: function (list) {
                let params={
                    mapCenter:[120.360456, 30.538622],
                    maxValue:100,
                    krigingModel:'exponential',//model还可选'gaussian','spherical'exponential
                    krigingSigma2:0,
                    krigingAlpha:100,
                    canvasAlpha:1,//canvas图层透明度
                    colors:["#006837", "#1a9850", "#66bd63", "#a6d96a", "#d9ef8b", "#ffffbf",
                        "#fee08b", "#fdae61", "#f46d43", "#d73027", "#a50026"],
                };
                let WFSVectorSource=new ol.source.Vector();
                let WFSVectorLayer=new ol.layer.Vector(
                    {
                        source:WFSVectorSource,
                    });
                map.addLayer(WFSVectorLayer);

//添加选择和框选控件，按住Ctrl/⌘键，使用鼠标框选采样点
                let select = new ol.interaction.Select();
                map.addInteraction(select);
                let dragBox = new ol.interaction.DragBox({
                    condition: ol.events.condition.platformModifierKeyOnly
                });
                map.addInteraction(dragBox);

//创建10个位置随机、属性值随机的特征点
                let a=new Array(10)
                for (let i = 0; i < list.length; i++) {
                    a[i]=new Array(3)
                    // a[i][0]=list[i].lng
                    // a[i][1]=list[i].lat
                    // a[i][2]=list[i].modal1

                    a[i][0]=params.mapCenter[0]+Math.random()*0.01-.005
                    a[i][1]=params.mapCenter[1]+Math.random()*0.01-.005
                    a[i][2]=Math.round(Math.random()*params.maxValue)

                    let feature = new ol.Feature({
                        geometry: new ol.geom.Point([a[i][0],a[i][1]]), value: a[i][2]
                    });
                    feature.setStyle(new ol.style.Style({
                        image: new ol.style.Circle({
                            radius: 1,
                            fill: new ol.style.Fill({color: "#00F"})
                        })
                    }));
                    WFSVectorSource.addFeature(feature);
                }


// //设置框选事件
//                 let selectedFeatures = select.getFeatures();
//                 dragBox.on('boxend', ()=>{
//                     let extent = dragBox.getGeometry().getExtent();
//                     WFSVectorSource.forEachFeatureIntersectingExtent(extent, (feature)=> {
//                         selectedFeatures.push(feature);
//                     });
//                     drawKriging(extent);
//                 });
//                 dragBox.on('boxstart', ()=>{
//                     selectedFeatures.clear();
//                 });

//绘制kriging插值图
                let j=0;
                let canvasLayer=null;
                const drawKriging=(extent)=>{
                    let values=[],lngs=[],lats=[];
                    // selectedFeatures.forEach(feature=>{
                    //     values.push(a[j][2]);
                    //     lngs.push(a[j][0]);
                    //     lats.push(a[j][1]);
                    //     j+=1
                    // });
                    for (let j=0;j<a.length;j++){
                        values.push(a[j][2]);
                            lngs.push(a[j][0]);
                            lats.push(a[j][1]);
                    }

                    if (values.length>3){
                        let variogram=kriging.train(values,lngs,lats,
                            params.krigingModel,params.krigingSigma2,params.krigingAlpha);

                        let polygons=[];
                        polygons.push([[extent[0],extent[1]],[extent[0],extent[3]],
                            [extent[2],extent[3]],[extent[2],extent[1]]]);
                        let grid=kriging.grid(polygons,variogram,(extent[2]-extent[0])/200);

                        let dragboxExtent=extent;
                        //移除已有图层
                        if (canvasLayer !== null){
                            map.removeLayer(canvasLayer);
                        }
                        //创建新图层
                        canvasLayer=new ol.layer.Image({
                            source: new ol.source.ImageCanvas({
                                canvasFunction:(extent, resolution, pixelRatio, size, projection) =>{
                                    let canvas = document.createElement('canvas');
                                    canvas.width = size[0];
                                    canvas.height = size[1];
                                    canvas.style.display='block';
                                    //设置canvas透明度
                                    canvas.getContext('2d').globalAlpha=params.canvasAlpha;

                                    //使用分层设色渲染
                                    kriging.plot(canvas,grid,
                                        [extent[0],extent[2]],[extent[1],extent[3]],params.colors);

                                    return canvas;
                                },
                                projection: 'EPSG:4326'
                            })
                        })
                        //向map添加图层
                        map.addLayer(canvasLayer);
                    }else {
                        alert("有效样点个数不足，无法插值");
                    }
                }
//首次加载，自动渲染一次差值图
                let extent = [params.mapCenter[0]-.005,params.mapCenter[1]-.005,params.mapCenter[0]+.005,params.mapCenter[1]+.005];
                WFSVectorSource.forEachFeatureIntersectingExtent(extent, (feature)=> {
                    // selectedFeatures.push(feature);
                });
                drawKriging(extent);
            },
            error: function () {
                alert("error");
            }
        })
    })

})