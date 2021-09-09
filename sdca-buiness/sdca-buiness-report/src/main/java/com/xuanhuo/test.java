package com.xuanhuo;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class test {
    public static void main(String[] args){
        System.out.println(getIDNo("报警人刘泽炜（男，户籍所在地：山东省淄博市周村村南郊镇方贾村21   ，现住址：济南市历下区泉城路180号齐鲁国际大厦如家酒店 ，身份证号码： 370302200003223918，联系电话：18912011901，现在济南市历下区泉城路如家酒店干前台）2021年5月28日下午13点43分左右，我接到一个电环称我在抖音上购买的香水出了质量问题，并且对方能够准确的说我的订单号，说他是抖音售后客服人员，抖音公司帮助我联系商家退费，当时我一听对方说的我的订单号是准确的，我就认为对方就是抖音的客服人员了，这时候对方要了我的支付宝，说要通过支付宝给我退钱，我就把自己的支付宝号码告知对方了，对方说我的支付宝没有开通商家的退款服务，要开通这个服务需要一定的银行流水，建议我到360借条刷一些流水，为了能够让对方帮我解决香水的问题，我就同意了对方的建议，对方说加入有其他情况可以联系他们的客服，他留给我一个电话和姓名，等了一会我的钱一直没有到账，于是我就打对方提供我的电话，电话那边的人说他不是抖音的客服，他不知道我转钱的事，于是我感到自己被骗了，就打电话报警了。民警已做询问笔录，报立刑事案件。"));
    }

    private static String getIDNo(String content){
        String regex = "\\d{17}[\\d|x]|\\d{15}" ;
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(content);
        if(m.find()){
            return m.group();
        }
        return "";
    }
}
