package com.example.libtouch.API;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.lang.Integer;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;

public class LibraryAPI {

    public int loggingIn(String urlparam, String dbparam, String usernameparam, String passwordparam) throws MalformedURLException, XmlRpcException {
        String url = urlparam;

        final XmlRpcClient client = new XmlRpcClient();

        final XmlRpcClientConfigImpl common_config = new XmlRpcClientConfigImpl();
        common_config.setServerURL(new URL(String.format("%s/xmlrpc/2/common", url)));
        client.execute(common_config, "version", emptyList());

        int uid = (int)client.execute(common_config, "authenticate", asList(dbparam, usernameparam, passwordparam, emptyMap()));

        return uid;
    }
    public String[] BorrowRequestInfo(final String url, String libraryID, String bookID, String db, int uid, String password) {

        List<Object> parseStudentIDarray = new ArrayList<>();
        List<Object> returnarray = new ArrayList<>();

        final XmlRpcClient models = new XmlRpcClient() {{
            setConfig(new XmlRpcClientConfigImpl() {{
                try {
                    setServerURL(new URL(String.format("%s/xmlrpc/2/object", url)));
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }});
        }};

        try {
            models.execute("execute_kw", asList(
                    db, uid, password,
                    "op.library.card", "check_access_rights",
                    asList("read"),
                    new HashMap() {{
                        put("raise_exception", false);
                    }}
            ));
        } catch (XmlRpcException e) {
            e.printStackTrace();
        }

        List<Object> StudentIDobject = null;
        List<Object> StudName = null;
        try {
            StudentIDobject = asList((Object[]) models.execute("execute_kw", asList(
                    db, uid, password,
                    "op.library.card", "search_read",
                    asList(asList(
                            asList("id", "=", libraryID))),

                    new HashMap() {{
                        put("fields", asList("student_id"));
                    }}
            )));
        } catch (XmlRpcException e) {
            e.printStackTrace();
        }

        List<Object> BookName = null;
        try {
            BookName = asList((Object[]) models.execute("execute_kw", asList(
                    db, uid, password,
                    "op.media", "search_read",
                    asList(asList(
                            asList("id", "=", bookID))),
                    new HashMap(){{
                        put("fields", asList("name"));
                    }}
            )));
        } catch (XmlRpcException e) {
            e.printStackTrace();
        }

        if(StudentIDobject.size() == 0) {
            Object obj = "{name=null, id=null}";
            parseStudentIDarray.add(obj);
        }
        else {
            parseStudentIDarray.add(StudentIDobject.get(0));
        }

        if(BookName.size() == 0) {
            Object obj = "{name=null, id=null}";
            parseStudentIDarray.add(obj);
        }
        else {
            parseStudentIDarray.add(BookName.get(0));
        }

        String[] splitData = parseStudentIDarray.toString().replaceAll("\\{","")
                .replaceAll("\\}","")
                .replaceAll("\\[","")
                .replaceAll("\\]","").split("\\,");
        String[] splitContent = new String[splitData.length];
        for(int i = 0;i<splitData.length;i++){
            System.out.println(splitData[i]);
            splitContent[i] = splitData[i].split("=")[1];
        }

        int studentID = Integer.valueOf(splitContent[0]);

        try {
            StudName = asList((Object[]) models.execute("execute_kw", asList(
                    db, uid, password,
                    "op.student", "search_read",
                    asList(asList(
                            asList("id", "=", studentID))),

                    new HashMap() {{
                        put("fields", asList("name"));
                    }}
            )));
        } catch (XmlRpcException e) {
            e.printStackTrace();
        }

        if(StudName.size() == 0) {
            Object obj = "{name=null, id=null}";
            returnarray.add(obj);
        }
        else {
            returnarray.add(StudName.get(0));
        }

        if(BookName.size() == 0) {
            Object obj = "{name=null, id=null}";
            returnarray.add(obj);
        }
        else {
            returnarray.add(BookName.get(0));
        }

        if(StudentIDobject.size() == 0) {
            Object obj = "{name=null, id=null}";
            returnarray.add(obj);
        }
        else {
            returnarray.add(StudentIDobject.get(0));
        }

        String[] splitDatanew = returnarray.toString().replaceAll("\\{","")
                .replaceAll("\\}","")
                .replaceAll("\\[","")
                .replaceAll("\\]","").split("\\,");
        String[] splitContentnew = new String[splitDatanew.length];
        for(int i = 0;i<splitDatanew.length;i++){
            System.out.println(splitDatanew[i]);
            splitContentnew[i] = splitDatanew[i].split("=")[1];
        }

        String[] borrowInfo = new String[2];
        borrowInfo[0] = splitContentnew[0];
        borrowInfo[1] = splitContentnew[2];

//        return fetchInfo;
        return borrowInfo;
    }



//    public void newMediaMovement(String studID, String BookID, SimpleDateFormat timestamp) {

}

