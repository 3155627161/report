package servlet;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;

public class QueryServlet extends HttpServlet {
private static final long serialVersionUID = 1L;

@Override
protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
	JSONArray json=GetData(10, 0);
    resp.setCharacterEncoding("UTF-8");  
    resp.setContentType("application/json; charset=utf-8");  
    // 转成数据流
    InputStream is = new ByteArrayInputStream(json.toString().getBytes());
    // 输出到画面
    ServletOutputStream op = resp.getOutputStream();
    int len;
    byte[] buff = new byte[4096];
    while ((len = is.read(buff)) != -1) {
        op.write(buff, 0, len);
    }
    op.flush();
}

@Override
protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
	doGet(req, resp);
}

public JSONArray GetData(int limit, int offset)
{
	List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
	Map<String,Object> map=new HashMap<String, Object>();
	map.put("ID", 1);
	map.put("Name", "cheng");
	map.put("Sex", "男");
	list.add(map);
	map.put("ID", 2);
	map.put("Name", "zhang");
	map.put("Sex", "女");
	list.add(map);
    int total = list.size();
    int rows = 2;
    Map<String,Object> map2=new HashMap<String, Object>();
    map2.put("total", total);
    map2.put("rows", rows);
    JSONArray json = JSONArray.fromObject(list);
    json.add(map);
    return json;
}

}