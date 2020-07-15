package com.demo2.demo.control;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import com.demo2.demo.dao.EmployeeDao;
import com.demo2.demo.entities.BoEntity;
import com.demo2.demo.entities.ExcelBean;
import com.demo2.demo.entities.Point;
import com.demo2.demo.entities.UsersEntity;
import com.demo2.demo.respority.boResp;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.server.Session;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
public class logincontrol {

    @Autowired
    EmployeeDao employeeDao;
    @Autowired
    com.demo2.demo.respority.boResp boResp;
    @PostMapping(value = "/user/login")
    public String login(@RequestParam("username") String username, @RequestParam("password") String password, Map<String,Object> map, HttpSession session){
        session.setAttribute("loginName",username);
        System.out.println("come");
        UsersEntity user = employeeDao.check(username,password);
        if (user!=null)
        {
//            if(user.getLastLng()!=null) {
//                session.setAttribute("last_lng", user.getLastLng());
//                session.setAttribute("last_lat", user.getLatLat());
//            }
            return "redirect:/index.html";
        }
        else
            {
                map.put("msg","登录失败");
                return "login";
        }
    }
        //可以用此方法设置首页
    @RequestMapping({"/"})
    public String index(){
        return "login";
    }

    //可以用此方法设置首页
    @RequestMapping({"/seaMap"})
    public String seaMap(){
        return "maps/shipxy";
    }

    @RequestMapping({"/heatMap"})
    public String heatMap(){
        return "maps/heatMap";
    }

    @RequestMapping({"/line"})
    public String lineMap(Model model){

        try{
            String pathname = "C:\\study\\bf.txt"; // 绝对路径或相对路径都可以，这里是绝对路径，写入文件时演示相对路径
            File filename = new File(pathname);
            InputStreamReader reader = new InputStreamReader(
                    new FileInputStream(filename)); // 建立一个输入流对象reader
            BufferedReader br = new BufferedReader(reader); // 建立一个对象，它把文件内容转成计算机能读懂的语言
            String line = "";
//            StringBuffer lineBuffer = new StringBuffer();
            ArrayList<Point> list=new ArrayList<>();
            while ((line = br.readLine()) != null) {
                if (!line.contains("-999000000.000000")){
                    Point point=new Point(Double.parseDouble(line.substring(0,9)),Double.parseDouble(line.substring(11,18)));
                    list.add(point);
//                    lineBuffer.
                }
            }
            model.addAllAttributes(list);
//            System.out.println(lineBuffer.toString());
        }catch (Exception e){
            e.printStackTrace();
        }
        return "maps/lineHeatMap";
    }

    @RequestMapping({"/bo"})
    public void bo(HttpServletResponse response) throws IOException {
        List<BoEntity> all = boResp.findAll();
        List<ExcelBean> list = new ArrayList<>();
        SimpleDateFormat ft = new SimpleDateFormat("yyyyMMdd");
        for(int i=0;i<all.size();){
            BoEntity boEntity = all.get(i);
            int j=i;
            BigDecimal tmp=new BigDecimal(0);
            String time = ft.format(boEntity.getTime());
            while(ft.format(boEntity.getTime()).equals(ft.format(all.get(j).getTime()))){
//                tmp+=all.get(j).getHpcp();
                tmp=tmp.add(all.get(j).getHpcp());
                j++;
            }
            list.add(new ExcelBean(tmp,time));
            i=j+1;
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



    @RequestMapping({"/po"})
    private void obs(){
        // 定义一个数据格式化对象
        XSSFWorkbook wb = null;
        try {
            //excel模板路径
            File cfgFile = ResourceUtils.getFile(ResourceUtils.CLASSPATH_URL_PREFIX + "static/asserts/2_obs_sta_jja_pre_553_1961_2005.csv");
            InputStream in = new FileInputStream(cfgFile);
            //读取excel模板
            wb = new XSSFWorkbook(in);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //获取sheet表格，及读取单元格内容
        XSSFSheet sheet = null;
        try{
            sheet = wb.getSheetAt(0);
            //先将获取的单元格设置为String类型，下面使用getStringCellValue获取单元格内容
            //如果不设置为String类型，如果单元格是数字，则报如下异常
            //java.lang.IllegalStateException: Cannot get a STRING value from a NUMERIC cell
            sheet.getRow(2).getCell(2).setCellType(CellType.STRING);
            //读取单元格内容
            String cellValue = sheet.getRow(2).getCell(2).getStringCellValue();


        }
        catch (Exception e){
            e.printStackTrace();
        }
    }




}
