package webserver;

/**
 * Created by JuHyunLee on 2017. 4. 8..
 */
public class HttpRequestTest {

//    @Test(expected = RuntimeException.class)
//    public void 헤더_기본조건_실패() {
//        new HttpRequest("/ HTTP/1.1");
//    }
//
//    @Test
//    public void GET_메소드파싱() {
//        HttpRequest httpRequest = new HttpRequest("GET / HTTP/1.1");
//        Assert.assertEquals(httpRequest.getMethod(), HttpRequest.Method.GET);
//    }
//
//    @Test(expected = IllegalArgumentException.class)
//    public void 메소드파싱_실패() {
//        new HttpRequest("UNKNOWN / HTTP/1.1");
//    }
//
//    @Test
//    public void path_파싱() {
//        HttpRequest httpRequest = new HttpRequest("GET / HTTP/1.1");
//        Assert.assertEquals(httpRequest.getPath(), "/");
//
//        HttpRequest httpRequest2 = new HttpRequest("GET /user/1 HTTP/1.1");
//        Assert.assertEquals(httpRequest2.getPath(), "/user/1");
//
//        HttpRequest httpRequest3 = new HttpRequest("GET /user/1/ HTTP/1.1");
//        Assert.assertEquals(httpRequest3.getPath(), "/user/1/");
//    }
//
//    @Test
//    public void 단순_split_테스트() {
//        Assert.assertEquals("a,b,c".split(",").length, 3);
//        Assert.assertEquals("a,b,c".split(",", 1).length, 1);
//
//        Assert.assertEquals("=".split("=", -2).length, 2);
//
//        Assert.assertEquals("Host: localhost:8080".split(": ", -2).length, 2);
//        Assert.assertEquals("Host localhost:8080".split(": ", -2).length, 2);
//    }
//
//    @Test
//    public void param_파싱() {
//        HttpRequest httpRequest = new HttpRequest("GET /user/1?a=b&c=d&e=&f=1&?foo=bar HTTP/1.1");
//        Assert.assertEquals(httpRequest.getParam("a"), "b");
//        Assert.assertEquals(httpRequest.getParam("c"), "d");
//        Assert.assertEquals(httpRequest.getParam("e"), "");
//        Assert.assertEquals(httpRequest.getParam("f"), "1");
//        Assert.assertEquals(httpRequest.getParam("?foo"), "bar");
//    }
//
//    @Test
//    public void 헤더파싱() {
//        String header = "POST /user/create HTTP/1.1\n" +
//                "Host: localhost:8080\n" +
//                "Connection: keep-alive\n" +
//                "Content-Length: 11\n" +
//                "Content-Type: application/x-www-form-urlencoded\n" +
//                "Accept: */*\n";
//        HttpRequest httpRequest = new HttpRequest(header);
//        String contentLength = httpRequest.getHeader("Content-Length");
//        Assert.assertNotNull(contentLength);
//        Assert.assertEquals(contentLength, "11");
//    }
//
//    @Test
//    public void POST_데이터_파싱() {
//        String header = "POST /user/create HTTP/1.1\n" +
//                "Host: localhost:8080\n" +
//                "Connection: keep-alive\n" +
//                "Content-Length: 11\n" +
//                "Content-Type: application/x-www-form-urlencoded\n" +
//                "Accept: */*\n";
//        HttpRequest httpRequest = new HttpRequest(header);
//
//        httpRequest.setRawData("foo=bar&a=1");
//        Assert.assertNotNull(httpRequest.getData("foo"));
//    }

}