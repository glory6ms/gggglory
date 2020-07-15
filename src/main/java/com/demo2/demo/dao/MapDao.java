package com.demo2.demo.dao;

import com.demo2.demo.entities.*;
import com.demo2.demo.respority.dynamicResp;
import com.demo2.demo.respority.obsResp;
import com.demo2.demo.respority.staticResp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.*;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Repository
public class MapDao {

    @Autowired
    staticResp staticResp;
    @Autowired
    dynamicResp dynamicResp;
    @Autowired
    com.demo2.demo.respority.obsResp obsResp;


    //根据时间段仅查询经纬度
    public Collection<DynamicEntity> queryLntByTime(String Timein, String Timeout){
        Date timein = null;
        Date timeout = null;
        try {
            timein = new SimpleDateFormat("yyyy-MM-dd").parse(Timein);
            timeout = new SimpleDateFormat("yyyy-MM-dd").parse(Timeout);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Collection<DynamicEntity> objects = dynamicResp.findBetweenTime(timein, timeout);
        return objects;
    }
    //根据时间段查询动态信息，查询d.mmsi,d.lng,d.lat,d.landCourse,d.landSpeed,d.shipState
    public Collection<DynamicEntity> queryStaticByTime(String Timein, String Timeout) {
//        Specification specification = new Specification() {
//            @Override
//            public Predicate toPredicate(Root root, CriteriaQuery criteriaQuery, CriteriaBuilder criteriaBuilder) {
//
//                Path time = root.get("time");
//                Date timein = null;
//                Date timeout = null;
//                try {
//                    timein = new SimpleDateFormat("yyyy-MM-dd").parse(Timein);
//                    timeout = new SimpleDateFormat("yyyy-MM-dd").parse(Timeout);
//
//                } catch (ParseException e) {
//                    e.printStackTrace();
//                }
//                Predicate predicate = criteriaBuilder.between(time, timein, timeout);
//                return predicate;
//            }
//        };
////        Sort sort = Sort.by(Sort.Order.desc("mmsi"));
////        String page ="0";
////        String size = "10";
////        Pageable pageable = PageRequest.of(Integer.parseInt(page), Integer.parseInt(size),sort);
////        Page<DynamicEntity> page1 = dynamicResp.findAll(specification, pageable);
//        Collection<DynamicEntity> list = dynamicResp.findAll(specification);
        //保存输入的时间段


        Date timein = null;
        Date timeout = null;
        try {
            timein = new SimpleDateFormat("yyyy-MM-dd").parse(Timein);
            timeout = new SimpleDateFormat("yyyy-MM-dd").parse(Timeout);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        Collection<DynamicEntity> objects = dynamicResp.findBetweenTime2(timein, timeout);
        return objects;
    }


    //根据时间段查询静态信息
    public Collection<StaticEntity> queryByTime(String Timein, String Timeout) {
        Specification specification = new Specification() {
            @Override
            public Predicate toPredicate(Root root, CriteriaQuery criteriaQuery, CriteriaBuilder criteriaBuilder) {

                Path time = root.get("time");
                Date timein = null;
                Date timeout = null;
                try {
//                    timein = new SimpleDateFormat("yyyy-MM-dd").parse("2017-02-04");
//
//                    timeout = new SimpleDateFormat("yyyy-MM-dd").parse("2017-02-10");

                    timein = new SimpleDateFormat("yyyy-MM-dd").parse(Timein);
                    timeout = new SimpleDateFormat("yyyy-MM-dd").parse(Timeout);

                } catch (ParseException e) {
                    e.printStackTrace();
                }
                Predicate predicate = criteriaBuilder.between(time, timein, timeout);
                return predicate;

            }
        };
        Collection<StaticEntity> list = staticResp.findAll(specification);
        return list;
    }

    //断面查询2
    public Collection<DynamicEntity> queryByArea2(Area area){
        BigDecimal lng1 = new BigDecimal(area.getLng1());
        BigDecimal lng2 = new BigDecimal(area.getLng2());
        BigDecimal lat1 = new BigDecimal(area.getLat1());
        BigDecimal lat2 = new BigDecimal(area.getLat2());
        Double A = Double.parseDouble(area.getLng1())-Double.parseDouble(area.getLng2());
        Double B = Double.parseDouble(area.getLat1())-Double.parseDouble(area.getLat2());
        Double C=A-B;
        Double k=B/A;
        Double b1=lat1.doubleValue()-k*(lng1.doubleValue());
        Double b2=lat2.doubleValue()-k*lng1.doubleValue();
//        List<DynamicEntity> allcollection = new ArrayList<>(area.getCollection());
//        List<DynamicEntity> collection = new ArrayList<>();
//        for(DynamicEntity d:allcollection){
//            if (distance(d.getLng().doubleValue(),d.getLat().doubleValue(),A,B,C)>1){
//                continue;
//            }else if (edgedistance(d.getLng().doubleValue(),d.getLat().doubleValue(),k,b1,b2)==0){
//                continue;
//            }else {
//                collection.add(d);
//            }
//        }
        BigDecimal y = lat1.subtract(lat2).divide(new BigDecimal("2")).pow(2);
        BigDecimal x = lng1.subtract(lng2).divide(new BigDecimal("2")).pow(2);
        //计算圆心经纬度
        BigDecimal cy = lat1.add(lat2).divide(new BigDecimal("2"));
        BigDecimal cx = lng1.add(lng2).divide(new BigDecimal("2"));
        //计算半径的平方
        BigDecimal z = x.add(y);
        //  System.out.println(z);

        List<DynamicEntity> allcollection = new ArrayList<>(area.getCollection());
        List<DynamicEntity> collection = new ArrayList<>();
        BigDecimal length=z;

        Iterator<DynamicEntity> it = allcollection.iterator();
        while (it.hasNext()) {
            BigDecimal len;
            DynamicEntity entity = it.next();
            //计算点到半径的距离
            //先添加条件排除锚泊状态的

            len = (entity.getLng().subtract(cx)).pow(2).add((entity.getLat().subtract(cy)).pow(2));
            Integer a = len.compareTo(length);
            if (a > 0) {
//                collection.remove(dynamicEntity);
//                    it.remove();
            }else{
                collection.add(entity);
            }
        }

        List<DynamicEntity> list = new ArrayList<>();
        for (int i=0;i<collection.size()-1;i++){
            if (collection.get(i).getMmsi()==collection.get(i+1).getMmsi()){
                if (judge(collection.get(i).getLng().doubleValue(),collection.get(i).getLat().doubleValue(),collection.get(i+1).getLng().doubleValue(),collection.get(i+1).getLat().doubleValue(),A,B,C)==1){
                    list.add(collection.get(i));
                }
            }else {
                BigDecimal landCourse = collection.get(i).getLandCourse();
                BigDecimal landSpeed = collection.get(i).getLandSpeed();
                double lc1 = Math.toRadians(landCourse.doubleValue());
                if (lc1 > 0) {
                    //画单位为30.8666m/min
//                        BigDecimal sp = landSpeed.multiply(new BigDecimal(30.8666 * 5));
                    BigDecimal x_sp = landSpeed.multiply(BigDecimal.valueOf(Math.sin(lc1))).setScale(5, RoundingMode.HALF_UP);
                    BigDecimal y_sp = landSpeed.multiply(BigDecimal.valueOf(Math.cos(lc1))).setScale(5, RoundingMode.HALF_UP);

                    //五分钟后的移动的距离
                    //距离是否需要转换为经纬度？
                    BigDecimal lng_new = x_sp.multiply(new BigDecimal(0.08)).divide(new BigDecimal(60),6, RoundingMode.HALF_UP);
                    BigDecimal lat_new = y_sp.multiply(new BigDecimal(0.08)).divide(new BigDecimal(60),6, RoundingMode.HALF_UP);
                    lng_new = lng_new.add(collection.get(i).getLng());
                    lat_new = lat_new.add(collection.get(i).getLat());
                    if (!intersection(lng1.doubleValue(), lat1.doubleValue(), lng2.doubleValue(), lat2.doubleValue(), collection.get(i).getLng().doubleValue(), collection.get(i).getLat().doubleValue(), lng_new.doubleValue(), lat_new.doubleValue())) {

                        System.out.println("不相交");

                    } else {
                        System.out.println("相交");
                        list.add(collection.get(i));
                    }
                }
            }
        }

        return  list;

    }
    public Double distance(Double lng,Double lat ,Double A,Double B,Double C){
        Double d=Math.abs(A*lng+B*lat+C)/Math.pow(Math.pow(A,2)+Math.pow(B,2),0.5);
        return d*60;
    }
    public int edgedistance(Double lng,Double lat,Double k,Double b1,Double b2){
        Double judge1=k*lng-lat+b1;
        Double judge2=k*lng-lat+b2;
        if (judge1*judge2>0){
            return 0;
        }else {
            return 1;
        }
    }
    public int judge(Double lng1,Double lat1,Double lng2,Double lat2,Double A,Double B,Double C){
        Double judge1=A*lng1+B*lat1+C;
        Double judge2=A*lng2+B*lat2+C;
        if (judge1*judge2>0){
            return 0;
        }else return 1;
    }

    //断面查询
    public Collection<DynamicEntity> queryByArea(Area area) {

        /**
         * 1.根据断面画圆，筛选出在圆域内的点：如果断面过长，则设定一个较小的固定圆域，如果过短，则补长
         * 此处计算半径长使用相似三角形：(lat1-lat2)/y=(lng1-lng2)/x=直径长/半径长
         * 保证所取圆域大于某个最小值，小于某个最大值（0.000003，0.0005）
         * 2.将相同mmsi号的动态信息存储分区，对每一艘船舶的轨迹判断是否过断面
         * 选取船舶轨迹中两侧离断面最近的点，然后连成线段1，判断线段和断面线段是否相交
         * 3.将所有通过断面的船舶归类
         *
         */
        BigDecimal lng1 = new BigDecimal(area.getLng1());
        BigDecimal lng2 = new BigDecimal(area.getLng2());
        BigDecimal lat1 = new BigDecimal(area.getLat1());
        BigDecimal lat2 = new BigDecimal(area.getLat2());
//        bigdecimal设置精度
//        MathContext mc = new MathContext(15);
        //x,y,z为直角三角形,计算的是,x,y,z的平方
        BigDecimal y = lat1.subtract(lat2).divide(new BigDecimal("2")).pow(2);
        BigDecimal x = lng1.subtract(lng2).divide(new BigDecimal("2")).pow(2);
        //计算圆心经纬度
        BigDecimal cy = lat1.add(lat2).divide(new BigDecimal("2"));
        BigDecimal cx = lng1.add(lng2).divide(new BigDecimal("2"));
        //计算半径的平方
        BigDecimal z = x.add(y);
        //  System.out.println(z);
        Integer ln = z.compareTo(new BigDecimal(0.0005));
        Integer lr = z.compareTo(new BigDecimal(0.000003));
        List<DynamicEntity> allcollection = new ArrayList<>(area.getCollection());
        List<DynamicEntity> collection = new ArrayList<>();
        BigDecimal length;
        if (ln > 0) {
            //半径大于0.0005，半径长2选用0.0005
            length = new BigDecimal("0.0005");
        } else if (lr < 0) {
            //半径小于0.000003,半径长2选用0.000003
            length = new BigDecimal("0.000003");
        } else {
            //选取的半径长度合适
            length = z;
        }
        Iterator<DynamicEntity> it = allcollection.iterator();
        while (it.hasNext()) {
            BigDecimal len;
            DynamicEntity entity = it.next();
            //计算点到半径的距离
            //先添加条件排除锚泊状态的

            len = (entity.getLng().subtract(cx)).pow(2).add((entity.getLat().subtract(cy)).pow(2));
            Integer a = len.compareTo(length);
            if (a > 0) {
//                collection.remove(dynamicEntity);
//                    it.remove();
            }else{
                collection.add(entity);
            }


        }
        //第二步判断船舶是否过断面
        //通过断面的船舶集合
        List<DynamicEntity> collection1 = new ArrayList<>();
        List<DynamicEntity> collection3 = new ArrayList<>();

        //此处是否一个-1？
        //原collection里需要把以筛选的Mmsi别开
        for (int i = 0; i < collection.size(); i++) {
            int flag = 0;
            if (!collection3.isEmpty()) {
                for (int o = 0; o < collection3.size(); o++) {
                    if (collection3.get(o).getMmsi().equals(collection.get(i).getMmsi())) {
                        flag = 1;
                        break;
                    }
                }
            }
            if (flag == 0) {
                List<DynamicEntity> collection2 = new ArrayList<>();
                collection2.add(collection.get(i));
                collection3.add(collection.get(i));
                for (int j = i + 1; j < collection.size(); j++) {
                    if (collection.get(j).getMmsi().equals(collection.get(i).getMmsi())) {
                        collection2.add(collection.get(j));
                    }
                }

                if (collection2.size() < 2) {
                    //按现有速度判断五分钟后，该船的位置，把两个点相连，判断是否过断面
                    // 需要验证一下！！
                    /**
                     * BigDecimal old_lng = tmp.getLng();
                     *                         BigDecimal old_lat = tmp.getLat();
                     *                         BigDecimal land_course = tmp.getLandCourse();
                     *                         BigDecimal land_speed = tmp.getLandSpeed();
                     *                         //行驶的航速
                     *                         BigDecimal x_sp = land_speed.multiply(BigDecimal.valueOf(Math.sin(land_course.doubleValue()*Math.PI/180))).setScale(5, RoundingMode.HALF_UP);
                     *                         BigDecimal y_sp = land_speed.multiply(BigDecimal.valueOf(Math.cos(land_course.doubleValue()*Math.PI/180))).setScale(5, RoundingMode.HALF_UP);
                     *                         System.out.println("x_sp:"+x_sp+"y_sp"+y_sp);
                     *                         //行驶了多少海里,0.010小时
                     *                         BigDecimal x_distance = x_sp.multiply(new BigDecimal(0.01)).divide(new BigDecimal(60),6, RoundingMode.HALF_UP);
                     *                         BigDecimal y_distance = y_sp.multiply(new BigDecimal(0.01)).divide(new BigDecimal(60),6, RoundingMode.HALF_UP);
                     *                         BigDecimal add_lng = old_lng.add(x_distance);
                     *                         BigDecimal add_lat = old_lat.add(y_distance);
                     */
                    BigDecimal landCourse = collection2.get(0).getLandCourse();
                    BigDecimal landSpeed = collection2.get(0).getLandSpeed();
                    double lc1 = Math.toRadians(landCourse.doubleValue());
                    if (lc1 > 0) {
                        //画单位为30.8666m/min
//                        BigDecimal sp = landSpeed.multiply(new BigDecimal(30.8666 * 5));
                        BigDecimal x_sp = landSpeed.multiply(BigDecimal.valueOf(Math.sin(lc1))).setScale(5, RoundingMode.HALF_UP);
                        BigDecimal y_sp = landSpeed.multiply(BigDecimal.valueOf(Math.cos(lc1))).setScale(5, RoundingMode.HALF_UP);

                        //五分钟后的移动的距离
                        //距离是否需要转换为经纬度？
                        BigDecimal lng_new = x_sp.multiply(new BigDecimal(0.08)).divide(new BigDecimal(60),6, RoundingMode.HALF_UP);
                        BigDecimal lat_new = y_sp.multiply(new BigDecimal(0.08)).divide(new BigDecimal(60),6, RoundingMode.HALF_UP);
                        lng_new = lng_new.add(collection2.get(0).getLng());
                        lat_new = lat_new.add(collection2.get(0).getLat());
                        if (!intersection(lng1.doubleValue(), lat1.doubleValue(), lng2.doubleValue(), lat2.doubleValue(), collection2.get(0).getLng().doubleValue(), collection2.get(0).getLat().doubleValue(), lng_new.doubleValue(), lat_new.doubleValue())) {

                            System.out.println("不相交");

                        } else {
                            System.out.println("相交");
                            collection1.add(collection2.get(0));
                        }
                    }
                } else {
                    //此处左右点筛选无误
                    List<DynamicEntity> left = new ArrayList<>();
                    List<DynamicEntity> right = new ArrayList<>();
                    for (int k = 0; k < collection2.size(); k++) {
                        Double tmp = (lat1.doubleValue() - lat2.doubleValue()) * collection2.get(k).getLng().doubleValue() + (lng2.doubleValue() - lng1.doubleValue()) * collection2.get(k).getLat().doubleValue() + lng1.doubleValue() * lat2.doubleValue() - lng2.doubleValue() * lat1.doubleValue();
                        if (tmp > 0) {
                            left.add(collection2.get(k));

                        } else {
                            right.add(collection2.get(k));
                        }
                    }
                    if (left.size() != 0 && right.size() != 0) {
                        //此处选择排序选最近点无误
                        double leftlength[] = new double[left.size()];
                        double rightlength[] = new double[right.size()];
                        int leftnum[] = new int[left.size()];
                        int rightnum[] = new int[right.size()];
                        for (int lf = 0; lf < left.size(); lf++) {
                            leftlength[lf] = getLength(lng1.doubleValue(), lat1.doubleValue(), lng2.doubleValue(), lat2.doubleValue(), left.get(lf).getLng().doubleValue(), left.get(lf).getLat().doubleValue());
                            leftnum[lf] = lf;
                        }
                        for (int rf = 0; rf < right.size(); rf++) {
                            rightlength[rf] = getLength(lng1.doubleValue(), lat1.doubleValue(), lng2.doubleValue(), lat2.doubleValue(), right.get(rf).getLng().doubleValue(), right.get(rf).getLat().doubleValue());
                            rightnum[rf] = rf;
                        }
                        int lflag = 0;
                        int rflag = 0;
                        for (int q = 0; q < left.size(); q++) {
                            if (leftlength[q] < leftlength[lflag]) {
                                lflag = q;
                            }
                        }
                        for (int e = 0; e < right.size(); e++) {
                            if (rightlength[e] < rightlength[rflag]) {
                                rflag = e;
                            }
                        }
                        //System.out.println("判断是否相交。。。" + left.get(lflag).getLng().doubleValue() + "," + left.get(lflag).getLat().doubleValue() + "=" + right.get(rflag).getLng().doubleValue() + "," + left.get(lflag).getLat().doubleValue());

                        //相交判断无误
                        if (!intersection(lng1.doubleValue(), lat1.doubleValue(), lng2.doubleValue(), lat2.doubleValue(), left.get(lflag).getLng().doubleValue(), left.get(lflag).getLat().doubleValue(), right.get(rflag).getLng().doubleValue(), right.get(rflag).getLat().doubleValue())) {

                            System.out.println("不相交");

                        } else {
                            System.out.println("相交");
                            collection1.add(left.get(lflag));
                        }
                    }else {
                        //判断轨迹点都在一侧的情况下，船舶行驶五分钟能不能通过断面
                        DynamicEntity tmp = collection2.get(0);
                        for (DynamicEntity dy : collection2){

                            if(tmp.getTime().before(dy.getTime())){
                                tmp = dy;
                            }
                        }
                        BigDecimal old_lng = tmp.getLng();
                        BigDecimal old_lat = tmp.getLat();
                        BigDecimal land_course = tmp.getLandCourse();
                        BigDecimal land_speed = tmp.getLandSpeed();
                        //行驶的航速
                        BigDecimal x_sp = land_speed.multiply(BigDecimal.valueOf(Math.sin(Math.toRadians(land_course.doubleValue())))).setScale(5, RoundingMode.HALF_UP);
                        BigDecimal y_sp = land_speed.multiply(BigDecimal.valueOf(Math.cos(Math.toRadians(land_course.doubleValue())))).setScale(5, RoundingMode.HALF_UP);
                        System.out.println("x_sp:"+x_sp+"y_sp"+y_sp);
                        //行驶了多少海里,0.03小时
                        BigDecimal x_distance = x_sp.multiply(new BigDecimal(0.08)).divide(new BigDecimal(60),6, RoundingMode.HALF_UP);
                        BigDecimal y_distance = y_sp.multiply(new BigDecimal(0.08)).divide(new BigDecimal(60),6, RoundingMode.HALF_UP);
                        BigDecimal add_lng = old_lng.add(x_distance);
                        BigDecimal add_lat = old_lat.add(y_distance);
                        System.out.println(add_lng);
                        System.out.println(add_lat);
                        //相交判断无误
                        if (!intersection(lng1.doubleValue(), lat1.doubleValue(), lng2.doubleValue(), lat2.doubleValue(), add_lng.doubleValue(), add_lat.doubleValue(), old_lng.doubleValue(), old_lat.doubleValue())) {
                            System.out.println("不相交");
                        } else {
                            System.out.println("相交");
                            collection1.add(tmp);
                        }
                    }
                }
            }

        }
        return collection1;
    }


    private static Double getLenWithPoints(double p1x, double p1y, double p2x, double p2y) {
        Double length ;
        length = Math.sqrt(Math.pow(p2x - p1x, 2) + Math.pow(p2y - p1y, 2));
        return length;
    }

    public static Double getLength(double lx1, double ly1, double lx2,
                                   double ly2, double px, double py) {
        Double length ;
        double b = getLenWithPoints(lx1, ly1, px, py);
        double c = getLenWithPoints(lx2, ly2, px, py);
        double a = getLenWithPoints(lx1, ly1, lx2, ly2);

        if (c + b == a) {// 点在线段上
            length = (double) 0;
        } else {
            // 组成锐角三角形，则求三角形的高
            double p = (a + b + c) / 2;// 半周长
            double s = Math.sqrt(p * (p - a) * (p - b) * (p - c));// 海伦公式求面积
            length = 2 * s / c;// 返回点到线的距离（利用三角形面积公式求高）
        }
        return length;
    }

    public static boolean intersection(double l1x1, double l1y1, double l1x2, double l1y2,
                                       double l2x1, double l2y1, double l2x2, double l2y2) {
        // 快速排斥实验 首先判断两条线段在 x 以及 y 坐标的投影是否有重合。 有一个为真，则代表两线段必不可交。
        if (Math.max(l1x1, l1x2) < Math.min(l2x1, l2x2)
                || Math.max(l1y1, l1y2) < Math.min(l2y1, l2y2)
                || Math.max(l2x1, l2x2) < Math.min(l1x1, l1x2)
                || Math.max(l2y1, l2y2) < Math.min(l1y1, l1y2)) {
            return false;
        }
        // 跨立实验  如果相交则矢量叉积异号或为零，大于零则不相交
        if ((((l1x1 - l2x1) * (l2y2 - l2y1) - (l1y1 - l2y1) * (l2x2 - l2x1))
                * ((l1x2 - l2x1) * (l2y2 - l2y1) - (l1y2 - l2y1) * (l2x2 - l2x1))) > 0
                || (((l2x1 - l1x1) * (l1y2 - l1y1) - (l2y1 - l1y1) * (l1x2 - l1x1))
                * ((l2x2 - l1x1) * (l1y2 - l1y1) - (l2y2 - l1y1) * (l1x2 - l1x1))) > 0) {
            return false;
        }
        return true;
    }

    public Collection<StaticEntity> queryByDyMMSI(List<Integer> MMSI){
        List<StaticEntity> st = staticResp.findByMmsiIn(MMSI);

        return st;
    }
    public List<DynamicEntity> queryByMMSIandSory(List<Integer> MMSI,String Timein, String Timeout){
        Specification specification = new Specification() {
            @Override
            public Predicate toPredicate(Root root, CriteriaQuery criteriaQuery, CriteriaBuilder criteriaBuilder) {

                Path ms = root.get("mmsi");
                Path time = root.get("time");
                CriteriaBuilder.In<Object> in = criteriaBuilder.in(ms);
                for (int i=0;i<MMSI.size();i++){
                    in.value(MMSI.get(i));
                }
                Date timein = null,timeout = null;
                try {
                    timein = new SimpleDateFormat("yyyy-MM-dd").parse(Timein);
                    timeout = new SimpleDateFormat("yyyy-MM-dd").parse(Timeout);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                Predicate predicate = criteriaBuilder.and(in,criteriaBuilder.between(time, timein, timeout));
                return predicate;
            }
        };
        Sort sort = Sort.by(Sort.Order.asc("time"));
        List<DynamicEntity> all = dynamicResp.findAll(specification,sort);
        return all;

    }

    public List<DynamicEntity> queryByMMSIandName(Integer MMSI,String shipname){
        Specification specification = new Specification() {
            @Override
            public Predicate toPredicate(Root root, CriteriaQuery criteriaQuery, CriteriaBuilder criteriaBuilder) {

                Path ms = root.get("mmsi");
                Path sn = root.get("shipName");
                Predicate predicate;
                if (MMSI.compareTo(-1)!=0){
                    predicate = criteriaBuilder.equal(ms,MMSI);
                }else {
                    predicate =criteriaBuilder.equal(sn,shipname);
                }

                return predicate;
            }
        };
        Sort sort = Sort.by(Sort.Order.asc("time"));
        List<DynamicEntity> all = dynamicResp.findAll(specification,sort);
        return all;

    }


    //热点查询
    //根据时间段仅查询经纬度
    public Page<DynamicEntity> queryLntByTime(String Timein, String Timeout,int page,int size){
        Specification specification = new Specification() {
            @Override
            public Predicate toPredicate(Root root, CriteriaQuery criteriaQuery, CriteriaBuilder criteriaBuilder) {

                Path time = root.get("time");
                Date timein = null;
                Date timeout = null;
                try {
                    timein = new SimpleDateFormat("yyyy-MM-dd").parse(Timein);
                    timeout = new SimpleDateFormat("yyyy-MM-dd").parse(Timeout);

                } catch (ParseException e) {
                    e.printStackTrace();
                }
                Predicate predicate = criteriaBuilder.between(time, timein, timeout);
                return predicate;
            }
        };
        Sort sort = Sort.by(Sort.Order.desc("lng"));
        Pageable pageable = PageRequest.of(page, size,sort);
        Page<DynamicEntity> page1 = dynamicResp.findAll(specification, pageable);
        System.out.println(page1.getContent().size());
        return page1;
    }

    public List<LnglatEntity> query(){
        List<LnglatEntity> all = obsResp.findAll();
        return all;
    }
}
