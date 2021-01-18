package com.jica.bookproject;

import android.util.Log;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;


public class Api {

    static public String clientId = "5kRoy897CItOYxMogUYN"; //애플리케이션 클라이언트 아이디값"
    static public String clientSecret = "Yu3xbdA941"; //애플리케이션 클라이언트 시크릿값"

    String keyword = null;

    String responseBody; //여기서 responseBody를 선언하면  parseStart()에서 인자로 받을 필요가없다.



    Api(String keyword, int start) {

        try {
            keyword = URLEncoder.encode(keyword, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("검색어 인코딩 실패", e);
        }

        //String apiURL = "https://openapi.naver.com/v1/search/blog?query=" + keyword;    // json 결과
        //String apiURL = "https://openapi.naver.com/v1/search/blog.xml?query="+ keyword; // xml 결과
        String apiURL = "https://openapi.naver.com/v1/search/book_adv.xml?display=30&d_titl="+ keyword + "&start=" + start; //xml 결과(상세검색)
        //                                                                 요청변수설정은 여기서한다. &로 연결 - display - 결과갯수 start - 시작점
        //상세검색을 하기위해서는 상세검색용 URL 사용
        //상세 검색은 책 제목(d_titl), 저자명(d_auth), 목차(d_cont), ISBN(d_isbn), 출판사(d_publ) 5개 항목 중에서 1개 이상 값을 입력해야함.
        //제목으로 검색할것이기 때문에 ?d_titl= 을 추가해준다.

        Map<String, String> requestHeaders = new HashMap<>();
        requestHeaders.put("X-Naver-Client-Id", clientId);
        requestHeaders.put("X-Naver-Client-Secret", clientSecret);
        responseBody = get(apiURL, requestHeaders);

       //Log.d("TAG",responseBody);

    }

    private static String get(String apiUrl, Map<String, String> requestHeaders) {
        HttpURLConnection con = connect(apiUrl);
        try {
            con.setRequestMethod("GET");
            for (Map.Entry<String, String> header : requestHeaders.entrySet()) {
                con.setRequestProperty(header.getKey(), header.getValue());
            }

            int responseCode = con.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) { // 정상 호출
                return readBody(con.getInputStream());
            } else { // 에러 발생
                return readBody(con.getErrorStream());
            }
        } catch (IOException e) {
            throw new RuntimeException("API 요청과 응답 실패", e);
        } finally {
            con.disconnect();
        }
    }

    private static HttpURLConnection connect(String apiUrl) {
        try {
            URL url = new URL(apiUrl);
            return (HttpURLConnection) url.openConnection();
        } catch (MalformedURLException e) {
            throw new RuntimeException("API URL이 잘못되었습니다. : " + apiUrl, e);
        } catch (IOException e) {
            throw new RuntimeException("연결이 실패했습니다. : " + apiUrl, e);
        }
    }

    private static String readBody(InputStream body) {
        InputStreamReader streamReader = new InputStreamReader(body);

        try (BufferedReader lineReader = new BufferedReader(streamReader)) {
            StringBuilder responseBody = new StringBuilder();

            String line;
            while ((line = lineReader.readLine()) != null) {
                responseBody.append(line);
                //Log.d("TAG",line);
            }

            return responseBody.toString();
        } catch (IOException e) {
            throw new RuntimeException("API 응답을 읽는데 실패했습니다.", e);
        }
    }


    //둠파서 start
    //결국 parseStart()의 파싱한 결과값은 ArrayList<BookItem>이므로 리턴값을 ArrayList<BookItem>로 설정한다.
    ArrayList<BookItem> parseStart(){

        if(responseBody == null){ //아직 xml문서를 다운받기전
            return null;
        }


        ArrayList<BookItem> bookItems;

        try {

            //파싱결과를 저장할 컬렉션 객체
            bookItems = new ArrayList<BookItem>();

            BookItem bookItem = null;

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();   //싱글톤패턴 //			DocumentBuilderFactory객체
            DocumentBuilder builder = factory.newDocumentBuilder(); //DocumentBuilderFactory객체로  DocumentBuilder객체를 만듬

            InputStream istream = new ByteArrayInputStream(responseBody.getBytes("utf-8"));
            Document document = builder.parse(istream);

            Element order = document.getDocumentElement();

            NodeList total = order.getElementsByTagName("total"); //total을 구하기위함
            Node total1 = total.item(0);
            Node total2 = total1.getFirstChild();

            String nodeValue1 = total2.getNodeValue();

            //Log.d("TAG",nodeValue1);

            NodeList items = order.getElementsByTagName("item");



            //Log.d("TAG", responseBody);

            //Log.d("TAG", "item의 갯수 : " + items.getLength());


            BookItem.total = Integer.parseInt(nodeValue1);
            //BookItem에 static 으로 설정하면 여러번 반복저장되지않고 한번만저장된다.
            //미리 total값을 저장해놓을 수 있기때문에 위와같은 코드로 작성한다.
            for(int i=0; i < items.getLength(); i++){ //<item></item> 건수만큼 반복

                //1건의 정보를 저장할 객체생성
                bookItem = new BookItem();

                Node item = items.item(i);
                NodeList childNodes = item.getChildNodes();//<item></item>태그의 자식 노드들을 노드리스트로 만듬 - 총 10건을 만들게된다. <item> title, link ... ~ ~ ~ </item>


                for(int j=0; j<childNodes.getLength();j++) { //자식노드의 갯수만큼 - 10건 title, link ... ~~~

                    Node childItem = childNodes.item(j);
                    String nodeName = childItem.getNodeName();   // 태그의 이름을 얻어옴

                    Node text = childItem.getFirstChild(); //노드의 첫번째 자식 노드 (실질적인 값을 가지고있음)
                                 //chiledItem 자체에 내용값이 있는것이아닌 child에 들어가있다.


                    if(text == null){
                        continue; //다시 반복문의 처음으로 돌아감
                    }
                    //검색중 NullPointException이 발생하는 이유
                    //검색어마다 데이터가 넘어올때 <discount></discount> 처럼 정보가 null인경우도 존재한다
                    //이때는 null이 text로 넘어가기때문에
                    //위와같이 작성해준다.


                    String nodeValue = text.getNodeValue();    // 태그의 내용값을 얻어옴
                                     //자식.getNodeValue();

                    nodeValue = nodeValue.replaceAll("<(/)?([a-zA-Z]*)(\\\\s[a-zA-Z]*=[^>]*)?(\\\\s)*(/)?>","");
                    nodeValue = nodeValue.replaceAll("&lt;","<");
                    nodeValue = nodeValue.replaceAll("&gt;",">");
                    nodeValue = nodeValue.replaceAll("&apos;","\'");
                    nodeValue = nodeValue.replaceAll("&quot;","\"");

                    //얻어온 내용값에서 html태그를 제거한다.   정규식

                    //Log.d("TAG", nodeName + "," + nodeValue);

                    switch (nodeName){
                        case "title" :
                            bookItem.setTitle(nodeValue);
                            break;
                        case "author" :
                            bookItem.setAuthor(nodeValue);
                            break;
                        case "image" :
                            bookItem.setImage(nodeValue);
                            break;

                        case "publisher" :
                            bookItem.setPublisher(nodeValue);
                            break;

                        case "pubdate" :
                            bookItem.setPubdate(nodeValue);
                            break;

                        case "description" :
                            bookItem.setDescription(nodeValue);
                            break;


                    }

                }

                /*
                for(int k=0; k <items2.getLength(); k++){

                    Node item2 = items2.item(i); //

                    Node text2 = item2.getFirstChild(); //


                    String totalNodeValue = text2.getNodeValue();//

                    bookItem.setTotal(Integer.parseInt(totalNodeValue));//

                    Log.d("TAG","total : " + bookItem.getTotal());

                }
                */

                bookItems.add(bookItem);
               // Log.d("TAG",bookItem.toString());

            }


        } catch (Exception e) {
            e.printStackTrace();
            return null;  //파싱중 오류발생
        }

        return bookItems;

    }



}




