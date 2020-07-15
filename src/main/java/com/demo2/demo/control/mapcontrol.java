package com.demo2.demo.control;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import com.demo2.demo.compent.DBScan;
import com.demo2.demo.dao.MapDao;
import com.demo2.demo.entities.*;
import com.demo2.demo.respority.dynamicResp;
import com.demo2.demo.respority.modalResp;
import com.demo2.demo.respority.obsResp;
import com.demo2.demo.respority.staticResp;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.persistence.criteria.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


@Controller
public class mapcontrol {
    @InitBinder
    public void initListBinder(WebDataBinder binder)
    {
        // 设置需要包裹的元素个数，默认为256
        binder.setAutoGrowCollectionLimit(1024);
    }

    class Line{//线
        Point p1,p2;
        Line(Point p1,Point p2){this.p1=p1;this.p2=p2;}
        public Point getP1(){return p1;}
        public Point getP2(){return p2;}
    }
    List<Line> lines= new ArrayList<Line>();//点集pts的凸包

    @Autowired
    private MapDao mapDao;
    @Autowired
    private staticResp staticResp;
    @Autowired
    dynamicResp dynamicResp;
    @Autowired
    com.demo2.demo.respority.modalResp modalResp;

    @Autowired
    obsResp obsResp;

    @Autowired
    com.demo2.demo.respority.poiResp poiResp;



    @PostMapping("/maps")
    public String QueryByTime(Period period, Model model) {
        //数据库查询，使用model返回
        Collection<DynamicEntity> DynamicEntities = mapDao.queryStaticByTime(period.getTimein(), period.getTimeout());
        model.addAttribute("dynamicAIS", DynamicEntities);
        return "maps/shipMap";
    }

    //跳转查询页面
    @GetMapping("/ships")
    public String toQueryShipsPage() {
        return "maps/shipList";
    }
    //跳转查询页面
    @GetMapping("/hot")
    public String toHotPointsPage() {
        return "maps/hotPoints";
    }

    @GetMapping("/flowGL")
    public String toFlowPage(){return "maps/flowGL";}
    //跳转查询页面
    @GetMapping("/ship")
    public String toShipdetailPage() {

        return "maps/shipDetail";
    }

    //跳转查询页面
    @GetMapping("/obs")
    public String toObsPage() {
        return "maps/obs";
    }
    @GetMapping("/jst")
    public String tojstPage() {
        return "maps/jst";
    }


    @GetMapping("/dis")
    public String toDisPage() {
        return "analysis/dis";
    }

    @GetMapping("/test")
    public String totestPage() {
        return "analysis/test";
    }

    @GetMapping("/tracks")
    public String toQueryTrackPage() {
        return "maps/indexMap";
    }

    @GetMapping("/tracklist")
    public String toShowTrackList() {
        return "analysis/tracklist";
    }


    //ajax查询--按时间查询动态信息
    @ResponseBody
    @RequestMapping(value = "/ajax/queryByTime", method = {RequestMethod.POST, RequestMethod.GET})
    public  Collection<DynamicEntity> QueryByTime(HttpServletRequest request) {
        String timein = request.getParameter("timein");
        String timeout = request.getParameter("timeout");
        //原正常散点
        Collection<DynamicEntity> DynamicEntities = mapDao.queryLntByTime(timein, timeout);
//        System.out.println("ajax启动..");
        System.out.println("时间查询的数量：" + DynamicEntities.size());

        return DynamicEntities;
    }
    //ajax查询--按时间查询动态信息
    @ResponseBody
    @RequestMapping(value = "/ajax/heat", method = {RequestMethod.POST, RequestMethod.GET})
    public  ArrayList<DynamicEntity> heat() {
        ArrayList<DynamicEntity> list=new ArrayList<>();
        try{
            String pathname = "C:\\study\\bf.txt"; // 绝对路径或相对路径都可以，这里是绝对路径，写入文件时演示相对路径
            File filename = new File(pathname);
            InputStreamReader reader = new InputStreamReader(
                    new FileInputStream(filename)); // 建立一个输入流对象reader
            BufferedReader br = new BufferedReader(reader); // 建立一个对象，它把文件内容转成计算机能读懂的语言
            String line = "";
            while ((line = br.readLine()) != null) {
                if (!line.contains("-999000000.000000")){
//                    Point point=new Point(Double.parseDouble(line.substring(0,9)),Double.parseDouble(line.substring(11,18)));
                    DynamicEntity d=new DynamicEntity(new BigDecimal(line.substring(0,9)),new BigDecimal(line.substring(11,18)),line.substring(20,27));
                    list.add(d);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return list;
    }

    @RequestMapping(value = "/ajax/draw_line", method = {RequestMethod.POST})
    @ResponseBody
    public List<List> readLine(HttpServletRequest request){
        List<List> list=new ArrayList<>();
        String pathname=null;
        String id = request.getParameter("next");
        if (id.equals("1")){
            pathname="C:\\study\\depth\\route1.txt";
        }else if (id.equals("2")){
            pathname="C:\\study\\depth\\route2.txt";
        }else if(id.equals("3")){
            pathname="C:\\study\\depth\\route3.txt";
        }else if(id.equals("4")){
            pathname="C:\\study\\depth\\route4.txt";
        }else if(id.equals("5")){
            pathname="C:\\study\\depth\\route5.txt";
        }else if(id.equals("6")){
            pathname="C:\\study\\depth\\route6.txt";
        }else if(id.equals("7")){
            pathname="C:\\study\\depth\\route7.txt";
        }else if(id.equals("8")){
            pathname="C:\\study\\depth\\route8.txt";
        }else if(id.equals("9")){
            pathname="C:\\study\\depth\\route9.txt";
        }
        try{
            String line = "";
            File filename = new File(pathname);
            InputStreamReader reader = new InputStreamReader(
                    new FileInputStream(filename)); // 建立一个输入流对象reader
            BufferedReader br = new BufferedReader(reader); // 建立一个对象，它把文件内容转成计算机能读懂的语言
            while ((line = br.readLine()) != null) {
                List l =new ArrayList();
                l.add(Double.parseDouble(line.substring(10,18)));
                l.add(Double.parseDouble(line.substring(0,9)));
                list.add(l);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return list;
    }
    private ArrayList<Point> list5=new ArrayList<>();
    //多热力图绘制
    @RequestMapping(value = "/ajax/draw", method = {RequestMethod.POST})
    @ResponseBody
    public ArrayList<DynamicEntity> readHeat(HttpServletRequest request) {
        String pathname = null;
        ArrayList<DynamicEntity> list=new ArrayList<>();

        String id = request.getParameter("next");
        if (id.equals("1")){
            pathname="C:\\study\\depth\\hstomap1.txt";
        }else if (id.equals("2")){
            pathname="C:\\study\\depth\\hstomap2.txt";
        }else if(id.equals("3")){
            pathname="C:\\study\\depth\\hstomap3.txt";
        }else if(id.equals("4")){
            pathname="C:\\study\\depth\\hstomap4.txt";
        }else if(id.equals("5")){
            pathname="C:\\study\\depth\\hstomap5.txt";
        }else if(id.equals("6")){
            pathname="C:\\study\\depth\\hstomap6.txt";
        }else if(id.equals("7")){
            pathname="C:\\study\\depth\\hstomap7.txt";
        }else if(id.equals("8")){
            pathname="C:\\study\\depth\\hstomap8.txt";
        }else if(id.equals("9")){
            pathname="C:\\study\\depth\\hstomap9.txt";
        }
        try{
            File filename = new File(pathname);
            InputStreamReader reader = new InputStreamReader(
                    new FileInputStream(filename)); // 建立一个输入流对象reader
            BufferedReader br = new BufferedReader(reader); // 建立一个对象，它把文件内容转成计算机能读懂的语言
            String line = "";

            while ((line = br.readLine()) != null) {
                if (!line.contains("-999000000.000000")){
                    DynamicEntity d=new DynamicEntity(new BigDecimal(line.substring(0,9)).setScale(5,BigDecimal.ROUND_HALF_UP),new BigDecimal(line.substring(11,18)).setScale(5,BigDecimal.ROUND_HALF_UP),line.substring(20,27));
                    list.add(d);

                }

            }


        }catch (Exception e){
            e.printStackTrace();
        }
        return list;
    }
    private void d(Point p1,Point p2,List<Point> s){
        //s集合为空
        if(s.isEmpty()){
//            lines.add(new Line(p1,p2));
            list5.add(p2);
            return;
        }
//        if (s.size()==1){
//            list5.add(s.get(0));
//            return;
//        }
        //s集合不为空，寻找Pmax
        double area=0;
        double maxArea=0;
        Point pMax=null;

        for(int i=0;i<s.size();i++){
            area=getArea(p1,p2,s.get(i));//最大面积对应的点就是Pmax
            if(area>maxArea){
                pMax=s.get(i);
                maxArea=area;
            }
        }
        if (p1==null||pMax==null||p2==null){
            return;
        }
        //找出位于(p1, pMax)直线左边的点集s1
        //找出位于(pMax, p2)直线左边的点集s2

        List<Point> s1=new ArrayList<Point>();
        List<Point> s2=new ArrayList<Point>();
        Point p3=null;
        for(int i=0;i<s.size();i++){

            p3=s.get(i);

            BigDecimal lf=new BigDecimal(getArea(p1,pMax,p3)).setScale(10,BigDecimal.ROUND_DOWN);
            BigDecimal lr = new BigDecimal(getArea(pMax,p2,p3)).setScale(10,BigDecimal.ROUND_DOWN);
            double a1=getArea(p1,pMax,p3);
            double a2=getArea(pMax,p2,p3);

            if(lf.compareTo(new BigDecimal(0))>0){s1.add(p3);}
            else if(lr.compareTo(new BigDecimal(0))>0){s2.add(p3);}
        }
        //递归
        d(p1,pMax,s1);
        d(pMax,p2,s2);
    }

    // 当且仅当点p3位于直线(p1, p2)左侧时，表达式的符号为正
    private double getArea(Point p1,Point p2,Point p3) {
        // 三角形的面积等于返回值绝对值的二分之一
        // p1.x*p2.y+p2.x*p3.y+p3.x*p1.y-p1.y*p2.x-p2.y*p3.x-p3.y*p1.x
        return p1.getX() * p2.getY() + p2.getX() * p3.getY() + p3.getX() * p1.getY() - p1.getY() * p2.getX() - p2.getY() * p3.getX() - p3.getY() * p1.getX();
    }
    //ajax查询--按时间查询动态信息
    @ResponseBody
    @RequestMapping(value = "/ajax/dbscan", method = {RequestMethod.POST, RequestMethod.GET})
    public ArrayList<Point> dbscan(HttpServletRequest request, HttpSession session) {
        String timein = request.getParameter("timein");
        String timeout = request.getParameter("timeout");
        int size= Integer.parseInt(request.getParameter("next"));
        System.out.println(size);
        Page<DynamicEntity> page1=mapDao.queryLntByTime(timein, timeout,size,10000) ;
        int num=page1.getTotalPages();
        int total = (int) page1.getTotalElements();
        List<DynamicEntity> DynamicEntities = page1.toList();
        ArrayList<Point> list=new ArrayList<>();
        DBScan dbScan = new DBScan(0.005, 1000);
        for (DynamicEntity d:DynamicEntities){
            list.add(new Point(d.getLng().doubleValue(),d.getLat().doubleValue()));
        }
        dbScan.process(list);
        list.get(0).setNum(num);
        list.get(1).setNum(total);
        return list;
    }
    //ajax查询--按时间查询动态信息
    @ResponseBody
    @RequestMapping(value = "/try/ajax", method = {RequestMethod.POST, RequestMethod.GET})
    public Map<String,Object> dbscan_divide(@RequestBody ShipList shipListlist) {

        System.out.println(shipListlist.toString());
        Map<String,Object> map = null;
        DBScan dbScan = new DBScan(0.005,100);
//        dbScan.process(list);
        map.put("points",shipListlist);
        return map;
    }
    @ResponseBody
    @RequestMapping(value = "/ajax/line", method = {RequestMethod.POST, RequestMethod.GET})
    public List<List> line() throws ParseException {
//        Date timein = new SimpleDateFormat("yyyy-MM-dd").parse(request.getParameter("timein"));
//        Date timeout = new SimpleDateFormat("yyyy-MM-dd").parse(request.getParameter("timeout"));
//        List<DynamicEntity> list = new ArrayList<>(mapDao.queryLntByTime(timein, timeout));
//        List<DynamicEntity> list=new ArrayList<>(dynamicResp.findBetweenTime1(timein,timeout));
        List<List> list=new ArrayList<>();
        try{
            String pathname = "C:\\study\\route6.txt"; // 绝对路径或相对路径都可以，这里是绝对路径，写入文件时演示相对路径
            File filename = new File(pathname);
            InputStreamReader reader = new InputStreamReader(
                    new FileInputStream(filename)); // 建立一个输入流对象reader
            BufferedReader br = new BufferedReader(reader); // 建立一个对象，它把文件内容转成计算机能读懂的语言
            String line = "";
//            StringBuffer lineBuffer = new StringBuffer();
            while ((line = br.readLine()) != null) {
                    List l =new ArrayList();
                    l.add(Double.parseDouble(line.substring(10,20)));
                    l.add(Double.parseDouble(line.substring(0,9)));
//                String[] point = new String[line.substring(10,20),line.substring(0,9)]
//                DynamicEntity d=new DynamicEntity(new BigDecimal(line.substring(10,20)),new BigDecimal(line.substring(0,9)));
                    list.add(l);
//                    lineBuffer.
            }
            System.out.println(list.toString());
        }catch (Exception e){
            e.printStackTrace();
        }
        return list;

//        List<Integer> mid = new ArrayList();
//        List<List> tracks = new ArrayList<>();
//        for (int i=0;tracks.size()<20;i++){
//            int flag =0;
//            if (mid!=null){
//                if (mid.contains(list.get(i).getMmsi())){
//                    flag=1;
//                }
//            }
//            if (flag==0) {
//                mid.add(list.get(i).getMmsi());
//                List ship = new ArrayList();
//                ship.add(Math.random()*100);
//                for (int j = i ; j < list.size(); j++) {
//                    if (list.get(i).getMmsi().equals(list.get(j).getMmsi()) ) {
//                        ship.add(list.get(j).getLng().doubleValue()*1e5);
//                        ship.add(list.get(j).getLat().doubleValue()*1e5);
//                    }
////                    System.out.println(list.get(j).getTime());
//                }
//                if(ship.size()>3){
//                    tracks.add(ship);
//                }
//
//            }
//        }
//        System.out.println(tracks);
//        return tracks;
    }

    @ResponseBody
    @RequestMapping(value = "/ajax/static/queryByTime", method = {RequestMethod.POST, RequestMethod.GET})
    public List<Object[]> QueryStaticByTime(HttpServletRequest request) {
        String timein = request.getParameter("timein");
        String timeout = request.getParameter("timeout");
        List<Object[]> listBytime = staticResp.findListBytime(timein, timeout);
        System.out.println("查询静态结果:" + listBytime.size() + "条");
        return listBytime;
    }

    @ResponseBody
    @RequestMapping(value = "/ajax/queryTime", method = {RequestMethod.POST, RequestMethod.GET})
    public Collection<DynamicEntity> QueryTime() {
        String timein = "2017-10-09";
        String timeout = "2017-10-10";
        Collection<DynamicEntity> DynamicEntities = mapDao.queryLntByTime(timein, timeout);
//        System.out.println("ajax启动..");
        System.out.println("时间查询的数量：" + DynamicEntities.size());
        return DynamicEntities;
    }
    //统计界面查询船舶全部信息
    @ResponseBody
    @RequestMapping(value = "/ajax/analysis/queryByMMSI", method = {RequestMethod.POST, RequestMethod.GET})
    public Map<String, Object> QueryStaticByMMSI(@RequestBody Area area1) {
        Collection<DynamicEntity> data = area1.getCollection();
        System.out.println("断面查询结果:" + data.size());
        List<Integer> MMSI = new ArrayList<>();
        //先提取出所有的mmsi
        //这里优化一下
        for (DynamicEntity dy : data) {
            int mflag = 0;
            if (MMSI != null) {
                for (int m = 0; m < MMSI.size(); m++) {
                    if (MMSI.get(m).equals(dy.getMmsi())) {
                        mflag = 1;
                        break;
                    }
                }
            }
            if (mflag == 0) {
                MMSI.add(dy.getMmsi());
            }
        }
        //船舶种类查询
        Collection<StaticEntity> objects = mapDao.queryByDyMMSI(MMSI);
        Map<String, Object> map;
        map = new HashMap<>();

        //这里的循环改一下
        if (objects.size() > 0) {
            String[] type = {"不明", "帆船", "货船", "客轮", "油轮", "游艇", "拖轮", "渔船", "军用船", "高速艇"
                    , "执法船", "引航船", "冲置艇", "拖带船", "医疗救援船",
                    "港口补给船", "其他类型船舶", "疏浚或水下作业船", "配有防污设备船", "18号决议规定的船", "拖带船>200M B>25M", "船舶类型未记录"};
            Integer[] num = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
            Iterator<StaticEntity> it = objects.iterator();
            while (it.hasNext()) {
                StaticEntity entity = it.next();
                for (int j = 0; j < type.length; j++) {
                    if (entity.getShipType().equals(type[j])) {
                        num[j] += 1;
                        break;
                    }
                }
            }
            map.put("staticdata", objects);
            map.put("shiptype", type);
            map.put("shipnum", num);
        } else {
            map.put("shiptype", "-1");
            map.put("shipnum", "-1");
        }
        //查询动态信息
        List<DynamicEntity> dy = mapDao.queryByMMSIandSory(MMSI, area1.getPeriod().getTimein(), area1.getPeriod().getTimeout());
        List<Area> areas = new ArrayList<>();
        //list1是判断是否为已判断的mmsi
        List<DynamicEntity> list1 = new ArrayList<>();
        for (int o = 0; o < dy.size(); o++) {
            int lflag = 0;
            DynamicEntity d = dy.get(o);
            if (!list1.isEmpty()) {
                for (DynamicEntity q : list1) {
                    if (q.getMmsi().equals(d.getMmsi())) {
                        lflag = 1;
                        break;
                    }
                }
            }
            if (lflag == 0) {
                List<DynamicEntity> list = new ArrayList<>();
                list.add(d);
                list1.add(d);

                for (int p = o + 1; p < dy.size(); p++) {
                    if (d.getMmsi().equals(dy.get(p).getMmsi())) {
                        list.add(dy.get(p));
                    }
                }
                Area area = new Area(list);
                areas.add(area);
            }
        }
        System.out.println(MMSI);

        map.put("mmsi", MMSI);
        map.put("area", areas);
        return map;
//            map.put("shiptype",Arrays.asList(0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0));
    }

    //ajax查询--按断面查询查询
    @RequestMapping(value = "/ajax/sectionsearch", method = {RequestMethod.POST})
    @ResponseBody
    public Map<String, Object> QueryBySection(@RequestBody Area area,HttpSession session) {
        Map<String, Object> map;
        System.out.println("断面查询..");
        //按时间段查询动态信息
        Collection<DynamicEntity> d2 = mapDao.queryStaticByTime(area.getPeriod().getTimein(), area.getPeriod().getTimeout());
        area.setCollection(d2);
        //根据经纬度筛选
        Collection<DynamicEntity> collection = mapDao.queryByArea(area);
        map = count_ship(collection);
        System.out.println("断面查询结果:" + collection.size());
        session.setAttribute("result",collection);
        return map;
    }

    //截面查询
    @RequestMapping(value = "/ajax/section", method = {RequestMethod.POST})
    @ResponseBody
    public Map<String,Object> QuerySection(@RequestBody Area area,HttpSession session) {
        //按时间段查询动态信息
        Collection<DynamicEntity> d2 = mapDao.queryStaticByTime(area.getPeriod().getTimein(), area.getPeriod().getTimeout());
        Collection<DynamicEntity> collection = d2;
        Double lng1 = Double.valueOf(area.getLng1());
        Double lat1 = Double.valueOf(area.getLat1());

        Double lng2 = Double.valueOf(area.getLng2());
        Double lat2 = Double.valueOf(area.getLat2());
        Double left, right, top, bottom;

        if (lng1 > lng2) {
            left = lng2;
            right = lng1;
        } else {
            left = lng1;
            right = lng2;
        }
        if (lat1 > lat2) {
            top = lat1;
            bottom = lat2;
        } else {
            top = lat2;
            bottom = lat1;
        }
        Iterator<DynamicEntity> it = collection.iterator();
        while (it.hasNext()) {
            DynamicEntity entity = it.next();
//            x>rect.left && x<rect.right) && (y>rect.top && y<rect.bottom
            if ((entity.getLng().doubleValue() > left && entity.getLng().doubleValue() < right) &&
                    (entity.getLat().doubleValue() < top && entity.getLat().doubleValue() > bottom)) {


            } else {
                it.remove();
            }
        }
        System.out.println("截面查询结果" + collection.size());
        session.setAttribute("result",collection);
        Map<String,Object> map;
        map=count_ship(collection);
        return map;
    }

    //根据船名与mmsi查询船舶信息
    @RequestMapping(value = "/ajax/queryShip", method = {RequestMethod.POST})
    @ResponseBody
    public List<DynamicEntity> QuerystaticBymmsi(HttpServletRequest request) {
        Integer mmsi = Integer.valueOf(request.getParameter("mmsi"));
        String shipname = request.getParameter("shipname");
        List<DynamicEntity> collection = mapDao.queryByMMSIandName(mmsi,shipname);
        return collection;
    }



    /**
     *
     * @param response
     * @throws IOException
     * 导出excel测试
     */
    @GetMapping("/export")
    public void exportExcel(HttpServletResponse response,HttpSession session) throws IOException {

        Collection<DynamicEntity> result = (Collection<DynamicEntity>) session.getAttribute("result");
        System.out.println(result.toString());
        List<ExcelBean> list = new ArrayList<>();
        for (DynamicEntity dynamicEntity:result){
            list.add(new ExcelBean(dynamicEntity.getMmsi(),dynamicEntity.getLandSpeed(),dynamicEntity.getLandCourse()));

        }
        //导出的时候指明标题列名和sheet名字
        ExportParams params = new ExportParams("船舶信息", "AIS数据");
        Workbook workbook = ExcelExportUtil.exportExcel(params, ExcelBean.class, list);

        // 告诉浏览器用什么软件可以打开此文件
        response.setHeader("content-Type", "application/vnd.ms-excel");
        // 下载文件的默认名称
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("船舶数据表","UTF-8") + ".xls");
        //编码
        response.setCharacterEncoding("UTF-8");
        workbook.write(response.getOutputStream());
    }


    //统计
    public Map<String,Object> count_ship(Collection<DynamicEntity> collection){
        Map<String, Object> map;
        map = new HashMap<>();
        List<Integer> MMSI = new ArrayList<>();
        //先提取出所有去重的mmsi
        for (DynamicEntity dy : collection) {
            int mflag = 0;
            if (MMSI != null) {
                for (int m = 0; m < MMSI.size(); m++) {
                    if (MMSI.get(m).equals(dy.getMmsi())) {
                        mflag = 1;
                        break;
                    }
                }
            }
            if (mflag == 0) {
                MMSI.add(dy.getMmsi());
            }
        }
        Collection<StaticEntity> objects = mapDao.queryByDyMMSI(MMSI);
        if(objects.size()>0){
            /**
             * 统计项：
             * 1.船长
             * 2.吃水
             * 3.船舶种类
             */
            int[] water_num={0,0,0,0,0};
            int[] shipLength_num = {0,0,0,0,0,0};
            int[] shipType_num ;
            Iterator<StaticEntity> it = objects.iterator();
            while (it.hasNext()) {
                StaticEntity entity = it.next();
                if (entity.getShipLength()<30){
                    shipLength_num[0]+=1;
                }else if (entity.getShipLength()<60){
                    shipLength_num[1]+=1;
                }else if (entity.getShipLength()<90){
                    shipLength_num[2]+=1;
                }else if (entity.getShipLength()<120){
                    shipLength_num[3]+=1;
                }else if (entity.getShipLength()<150){
                    shipLength_num[4]+=1;
                }else {
                    shipLength_num[5]+=1;
                }

                if (entity.getWaterDepth().doubleValue()<3){water_num[0]+=1;}else if (entity.getWaterDepth().doubleValue()<6){water_num[1]+=1;}else if (entity.getWaterDepth().doubleValue()<9){water_num[2]+=1;}else if (entity.getWaterDepth().doubleValue()<12){water_num[3]+=1;}else {water_num[4]+=1;}
                map.put("waterdepth",water_num);
                map.put("shiplength",shipLength_num);
            }

        }else {

            map.put("shiplength",-1);
        }
//        session.setAttribute("result",collection);
        map.put("dlist",collection);
        return map;
    }



    @ResponseBody
    @RequestMapping(value = "/ajax/modal", method = {RequestMethod.POST, RequestMethod.GET})
    public List<Sheet1Entity> heat_modal(HttpServletResponse response) throws IOException{
        List<Sheet1Entity> query = modalResp.findAll();
        return query;



//        List<LnglatEntity> list = obsResp.findAll();
//        List<PoiEntity> all = poiResp.findAll();
//        int[] days=new int[20];
//        int[] pdays=new int[20];
//        BigDecimal[] t =new BigDecimal[20];
//        Double[] p95=new Double[20];
//        Double[] p95p=new Double[20];
//        for (int d=0;d<20;d++){
//            days[d]=0;
//            t[d]=new BigDecimal(0);
//            p95[d]=0.0;
//            p95p[d]=0.0;
//            pdays[d]=0;
//        }
//
//        for(int i=0;i<all.size();){
//            String year=all.get(i).getYear();
//            int j=i;
//
//
//            for(;j<all.size();j++){
//                if (year.equals(all.get(j).getYear())){
//                    t[0]=t[0].add(new BigDecimal(all.get(j).getA1()));
//                    t[1]=t[1].add(new BigDecimal(all.get(j).getA2()));
//                    t[2]=t[2].add(new BigDecimal(all.get(j).getA3()));
//                    t[3]=t[3].add(new BigDecimal(all.get(j).getA4()));
//                    t[4]=t[4].add(new BigDecimal(all.get(j).getA5()));
//                    t[5]=t[5].add(new BigDecimal(all.get(j).getA6()));
//                    t[6]=t[6].add(new BigDecimal(all.get(j).getA7()));
//                    t[7]=t[7].add(new BigDecimal(all.get(j).getA8()));
//                    t[8]=t[8].add(new BigDecimal(all.get(j).getA9()));
//                    t[9]=t[9].add(new BigDecimal(all.get(j).getA10()));
//                    t[10]=t[10].add(new BigDecimal(all.get(j).getA11()));
//                    t[11]=t[11].add(new BigDecimal(all.get(j).getA12()));
//                    t[12]=t[12].add(new BigDecimal(all.get(j).getA13()));
//                    t[13]=t[13].add(new BigDecimal(all.get(j).getA14()));
//                    t[14]=t[14].add(new BigDecimal(all.get(j).getA15()));
//                    t[15]=t[15].add(new BigDecimal(all.get(j).getA16()));
//                    t[16]=t[16].add(new BigDecimal(all.get(j).getA17()));
//                    t[17]=t[17].add(new BigDecimal(all.get(j).getA18()));
//                    t[18]=t[18].add(new BigDecimal(all.get(j).getA19()));
//                    t[19]=t[19].add(new BigDecimal(all.get(j).getA20()));
//
//
//                    if (new BigDecimal(all.get(j).getA1()).compareTo(new BigDecimal(50))>=0){
//                        days[0]++;
//                    }
//                    if (new BigDecimal(all.get(j).getA2()).compareTo(new BigDecimal(50))>=0){
//                        days[1]++;
//                    }
//                    if (new BigDecimal(all.get(j).getA3()).compareTo(new BigDecimal(50))>=0){
//                        days[2]++;
//                    }
//                    if (new BigDecimal(all.get(j).getA4()).compareTo(new BigDecimal(50))>=0){
//                        days[3]++;
//                    }if (new BigDecimal(all.get(j).getA5()).compareTo(new BigDecimal(50))>=0){
//                        days[4]++;
//                    }
//                    if (new BigDecimal(all.get(j).getA6()).compareTo(new BigDecimal(50))>=0){
//                        days[5]++;
//                    }
//                    if (new BigDecimal(all.get(j).getA7()).compareTo(new BigDecimal(50))>=0){
//                        days[6]++;
//                    }
//                    if (new BigDecimal(all.get(j).getA8()).compareTo(new BigDecimal(50))>=0){
//                        days[7]++;
//                    }
//                    if (new BigDecimal(all.get(j).getA9()).compareTo(new BigDecimal(50))>=0){
//                        days[8]++;
//                    }
//                    if (new BigDecimal(all.get(j).getA10()).compareTo(new BigDecimal(50))>=0){
//                        days[9]++;
//                    }
//                    if (new BigDecimal(all.get(j).getA11()).compareTo(new BigDecimal(50))>=0){
//                        days[10]++;
//                    }
//                    if (new BigDecimal(all.get(j).getA12()).compareTo(new BigDecimal(50))>=0){
//                        days[11]++;
//                    }
//                    if (new BigDecimal(all.get(j).getA13()).compareTo(new BigDecimal(50))>=0){
//                        days[12]++;
//                    }
//                    if (new BigDecimal(all.get(j).getA14()).compareTo(new BigDecimal(50))>=0){
//                        days[13]++;
//                    }
//                    if (new BigDecimal(all.get(j).getA15()).compareTo(new BigDecimal(50))>=0){
//                        days[14]++;
//                    }
//                    if (new BigDecimal(all.get(j).getA16()).compareTo(new BigDecimal(50))>=0){
//                        days[15]++;
//                    }
//                    if (new BigDecimal(all.get(j).getA17()).compareTo(new BigDecimal(50))>=0){
//                        days[16]++;
//                    }
//                    if (new BigDecimal(all.get(j).getA18()).compareTo(new BigDecimal(50))>=0){
//                        days[17]++;
//                    }
//                    if (new BigDecimal(all.get(j).getA19()).compareTo(new BigDecimal(50))>=0){
//                        days[18]++;
//                    }
//                    if (new BigDecimal(all.get(j).getA20()).compareTo(new BigDecimal(50))>=0){
//                        days[19]++;
//                    }
//
//
//                    if (new BigDecimal(all.get(j).getA1()).compareTo(new BigDecimal(0))==0){
//                        pdays[0]++;
//                    }
//                    if (new BigDecimal(all.get(j).getA2()).compareTo(new BigDecimal(0))==0){
//                        pdays[1]++;
//                    }
//                    if (new BigDecimal(all.get(j).getA3()).compareTo(new BigDecimal(0))==0){
//                        pdays[2]++;
//                    }
//                    if (new BigDecimal(all.get(j).getA4()).compareTo(new BigDecimal(0))==0){
//                        pdays[3]++;
//                    }if (new BigDecimal(all.get(j).getA5()).compareTo(new BigDecimal(0))==0){
//                        pdays[4]++;
//                    }
//                    if (new BigDecimal(all.get(j).getA6()).compareTo(new BigDecimal(0))==0){
//                        pdays[5]++;
//                    }
//                    if (new BigDecimal(all.get(j).getA7()).compareTo(new BigDecimal(0))==0){
//                        pdays[6]++;
//                    }
//                    if (new BigDecimal(all.get(j).getA8()).compareTo(new BigDecimal(0))==0){
//                        pdays[7]++;
//                    }
//                    if (new BigDecimal(all.get(j).getA9()).compareTo(new BigDecimal(0))==0){
//                        pdays[8]++;
//                    }
//                    if (new BigDecimal(all.get(j).getA10()).compareTo(new BigDecimal(0))==0){
//                        pdays[9]++;
//                    }
//                    if (new BigDecimal(all.get(j).getA11()).compareTo(new BigDecimal(0))==0){
//                        pdays[10]++;
//                    }
//                    if (new BigDecimal(all.get(j).getA12()).compareTo(new BigDecimal(0))==0){
//                        pdays[11]++;
//                    }
//                    if (new BigDecimal(all.get(j).getA13()).compareTo(new BigDecimal(0))==0){
//                        pdays[12]++;
//                    }
//                    if (new BigDecimal(all.get(j).getA14()).compareTo(new BigDecimal(0))==0){
//                        pdays[13]++;
//                    }
//                    if (new BigDecimal(all.get(j).getA15()).compareTo(new BigDecimal(0))==0){
//                        pdays[14]++;
//                    }
//                    if (new BigDecimal(all.get(j).getA16()).compareTo(new BigDecimal(0))==0){
//                        pdays[15]++;
//                    }
//                    if (new BigDecimal(all.get(j).getA17()).compareTo(new BigDecimal(0))==0){
//                        pdays[16]++;
//                    }
//                    if (new BigDecimal(all.get(j).getA18()).compareTo(new BigDecimal(0))==0){
//                        pdays[17]++;
//                    }
//                    if (new BigDecimal(all.get(j).getA19()).compareTo(new BigDecimal(0))==0){
//                        pdays[18]++;
//                    }
//                    if (new BigDecimal(all.get(j).getA20()).compareTo(new BigDecimal(0))==0){
//                        pdays[19]++;
//                    }
//
//
//                }else {
//                    break;
//                }
//            }
//            i=j+1;
////           tmp.add(t);
//        }
//
//
//
//        System.out.println("百分位");
//
//
//        Sort sort;
//        List<PoiEntity> all1;
//        List<List<PoiEntity>> all2 = new ArrayList<>();
//        sort = Sort.by(Sort.Order.desc("a1"));
//        all1 = poiResp.findAll(sort);
//        all2.add(all1);
//        p95[0]= Double.valueOf(all1.get(206).getA1());
//
//        sort = Sort.by(Sort.Order.desc("a2"));
//        all1 = poiResp.findAll(sort);
//        all2.add(all1);
//        p95[1]= Double.valueOf(all1.get(206).getA1());
//
//        sort = Sort.by(Sort.Order.desc("a3"));
//        all1 = poiResp.findAll(sort);
//        all2.add(all1);
//        p95[2]= Double.valueOf(all1.get(206).getA1());
//
//        sort = Sort.by(Sort.Order.desc("a4"));
//        all1 = poiResp.findAll(sort);
//        all2.add(all1);
//        p95[3]= Double.valueOf(all1.get(206).getA1());
//        sort = Sort.by(Sort.Order.desc("a5"));
//        all1 = poiResp.findAll(sort);
//        all2.add(all1);
//        p95[4]= Double.valueOf(all1.get(206).getA1());
//        sort = Sort.by(Sort.Order.desc("a6"));
//
//        all1 = poiResp.findAll(sort);
//        all2.add(all1);
//        p95[5]= Double.valueOf(all1.get(206).getA1());
//        sort = Sort.by(Sort.Order.desc("a7"));
//        all1 = poiResp.findAll(sort);
//        all2.add(all1);
//        p95[6]= Double.valueOf(all1.get(206).getA1());
//        sort = Sort.by(Sort.Order.desc("a8"));
//        all1 = poiResp.findAll(sort);
//        all2.add(all1);
//        p95[7]= Double.valueOf(all1.get(206).getA1());
//        sort = Sort.by(Sort.Order.desc("a9"));
//        all1 = poiResp.findAll(sort);
//        all2.add(all1);
//        p95[8]= Double.valueOf(all1.get(206).getA1());
//        sort = Sort.by(Sort.Order.desc("a10"));
//        all1 = poiResp.findAll(sort);
//        all2.add(all1);
//        p95[9]= Double.valueOf(all1.get(206).getA1());
//
//        sort = Sort.by(Sort.Order.desc("a11"));
//        all1 = poiResp.findAll(sort);
//        all2.add(all1);
//        p95[10]= Double.valueOf(all1.get(206).getA1());
//
//        sort = Sort.by(Sort.Order.desc("a12"));
//        all1 = poiResp.findAll(sort);
//        all2.add(all1);
//        p95[11]= Double.valueOf(all1.get(206).getA1());
//        sort = Sort.by(Sort.Order.desc("a13"));
//        all1 = poiResp.findAll(sort);
//        all2.add(all1);
//        p95[12]= Double.valueOf(all1.get(206).getA1());
//        sort = Sort.by(Sort.Order.desc("a14"));
//        all1 = poiResp.findAll(sort);
//        all2.add(all1);
//        p95[13]= Double.valueOf(all1.get(206).getA1());
//        sort = Sort.by(Sort.Order.desc("a15"));
//        all1 = poiResp.findAll(sort);
//        all2.add(all1);
//        p95[14]= Double.valueOf(all1.get(206).getA1());
//        sort = Sort.by(Sort.Order.desc("a16"));
//        all1 = poiResp.findAll(sort);
//        all2.add(all1);
//        p95[15]= Double.valueOf(all1.get(206).getA1());
//        sort = Sort.by(Sort.Order.desc("a17"));
//        all1 = poiResp.findAll(sort);
//        all2.add(all1);
//        p95[16]= Double.valueOf(all1.get(206).getA1());
//        sort = Sort.by(Sort.Order.desc("a18"));
//        all1 = poiResp.findAll(sort);
//        all2.add(all1);
//        p95[17]= Double.valueOf(all1.get(206).getA1());
//        sort = Sort.by(Sort.Order.desc("a19"));
//        all1 = poiResp.findAll(sort);
//        all2.add(all1);
//        p95[18]= Double.valueOf(all1.get(206).getA1());
//        sort = Sort.by(Sort.Order.desc("a20"));
//        all1 = poiResp.findAll(sort);
//        all2.add(all1);
//        p95[19]= Double.valueOf(all1.get(206).getA1());
//
//
//
//
//        for (int p=0;p<207;p++){
//            p95p[0]+=Double.parseDouble(all2.get(0).get(p).getA1());
//            p95p[1]+=Double.parseDouble(all2.get(1).get(p).getA2());
//            p95p[2]+=Double.parseDouble(all2.get(2).get(p).getA3());
//            p95p[3]+=Double.parseDouble(all2.get(3).get(p).getA4());
//            p95p[4]+=Double.parseDouble(all2.get(4).get(p).getA5());
//            p95p[5]+=Double.parseDouble(all2.get(5).get(p).getA6());
//            p95p[6]+=Double.parseDouble(all2.get(6).get(p).getA7());
//            p95p[7]+=Double.parseDouble(all2.get(7).get(p).getA8());
//            p95p[8]+=Double.parseDouble(all2.get(8).get(p).getA9());
//            p95p[9]+=Double.parseDouble(all2.get(9).get(p).getA10());
//            p95p[10]+=Double.parseDouble(all2.get(0).get(p).getA11());
//            p95p[11]+=Double.parseDouble(all2.get(11).get(p).getA12());
//            p95p[12]+=Double.parseDouble(all2.get(12).get(p).getA13());
//            p95p[13]+=Double.parseDouble(all2.get(13).get(p).getA14());
//            p95p[14]+=Double.parseDouble(all2.get(14).get(p).getA15());
//            p95p[15]+=Double.parseDouble(all2.get(15).get(p).getA16());
//            p95p[16]+=Double.parseDouble(all2.get(16).get(p).getA17());
//            p95p[17]+=Double.parseDouble(all2.get(17).get(p).getA18());
//            p95p[18]+=Double.parseDouble(all2.get(18).get(p).getA19());
//            p95p[19]+=Double.parseDouble(all2.get(19).get(p).getA20());
//        }
//
//        Double[] hit=new Double[20];
//        Double[] R95t =new Double[20];
//        for (int i=0;i<20;i++){
//          hit[i]=t[i].doubleValue()/pdays[i];
//          R95t[i]=p95p[i]/t[i].doubleValue();
//            System.out.printf(days[i]+",");
//        }
//        System.out.println("暴雨日数");
//
//
//        for (int i=0;i<20;i++){
//            list.get(i).setHeight(p95[i]);
//        }
//
////        List<ExcelBean> personList = new ArrayList<>();
////        for (int i = 0; i < 20; i++) {
////           personList.add(new ExcelBean(list.get(i).getId(),t[i],days[i],p95[i],p95p[i]));
////
////        }
////
////        //导出的时候指明标题列名和sheet名字
////        ExportParams params = new ExportParams("用户数据", "用户数据");
////
////
////        Workbook workbook = ExcelExportUtil.exportExcel(params, ExcelBean.class, personList);
////
////        // 告诉浏览器用什么软件可以打开此文件
////        response.setHeader("content-Type", "application/vnd.ms-excel");
////        // 下载文件的默认名称
////        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("用户数据表","UTF-8") + ".xls");
////        //编码
////        response.setCharacterEncoding("UTF-8");
////        workbook.write(response.getOutputStream());
//        return list;
    }

    //X54776	X54808	X54823	X54836	X54843	X54852	X54857	X54863	X54871	X54916	X54936	X54945	X55228	X55279	X55299	X55472	X55578	X55598	X55696	X55773




}


