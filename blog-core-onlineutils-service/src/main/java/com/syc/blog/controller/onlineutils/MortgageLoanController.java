package com.syc.blog.controller.onlineutils;

import com.syc.blog.controller.BaseController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;

@Controller
@RequestMapping("/util/mortgage/loan")
public class MortgageLoanController extends BaseController {


    @RequestMapping("")
    public String mortgage(ModelMap map, @RequestParam(value = "page",required = false,defaultValue = "1") Integer page
            , @RequestParam("bindId") Integer bindId) {
        byte type = 16;
        getCurrentCommentsListPage(map,page,bindId,type);
        map.put("bindId",bindId);
        return "onlineutils/mortagel_loan";
    }

    /**
     * 60 万贷款，贷款 30 年
     * 公积金：3.25%，不用公积金：5.63%
     * 等额本金
     * @param loanTotal 贷款总额
     * @param year 贷款年限
     * @param annualRate 年利率
     * @param type 还款方式 1：等额本金  2：等额本息
     * */
    public static void calcMortgage(BigDecimal loanTotal,BigDecimal annualRate,int year,int type){
        BigDecimal totalInterestAmount = BigDecimal.ZERO;
        BigDecimal originLoanTotal = BigDecimal.valueOf(loanTotal.doubleValue());
       if(type == 1){
           BigDecimal capital = loanTotal.divide(new BigDecimal(30 * 12), 2, BigDecimal.ROUND_HALF_UP);//本金
            for(int i=1;i<=year * 12;i++){
                BigDecimal interest = loanTotal.multiply(annualRate).divide(new BigDecimal(12),2, BigDecimal.ROUND_HALF_UP);//利息
                System.out.println("第 "+i+" 个月的本金："+capital);
                System.out.println("第 "+i+" 个月的利息："+interest);
                totalInterestAmount = totalInterestAmount.add(interest);
                System.out.println("第 "+i+" 个月的月供："+ interest.add(capital));
                System.out.println("-------------------------------");
                loanTotal = loanTotal.subtract(capital);
            }
           System.out.println("等额本金总利息为："+totalInterestAmount);
           System.out.println("还款总金额："+totalInterestAmount.add(originLoanTotal));
       }
       if(type == 2){
           // 10000  ×  [4.5‰×(1+4.5‰) ^ 24]  ÷  {[(1+4.5‰) ^ 24]-1}=440.51元。
           BigDecimal monthRate = annualRate.divide(new BigDecimal(12),10, BigDecimal.ROUND_HALF_UP);//月利率
           System.out.println(monthRate);
           double pow = Math.pow(new BigDecimal("1").add(monthRate).doubleValue(), year * 12);//（1+月利率）²

           System.out.println(pow);

           BigDecimal step2 = new BigDecimal(String.valueOf(pow)).subtract(new BigDecimal("1"));
           System.out.println(step2);

           BigDecimal interest = loanTotal.multiply(monthRate).multiply(new BigDecimal(String.valueOf(pow))).divide(step2, 2, BigDecimal.ROUND_HALF_UP);//利息
           for(int i= 1;i<=year * 12 ;i++){
               BigDecimal lixi = loanTotal.multiply(annualRate).divide(new BigDecimal(12),2,BigDecimal.ROUND_HALF_UP);
               System.out.println("第 "+i+" 个月利息："+lixi);
               System.out.println("第 "+i+" 个月本金："+interest.subtract(lixi));
               System.out.println("第 "+i+" 个月的月供："+interest);
               System.out.println("-------------------------------");
               loanTotal = loanTotal.subtract(interest.subtract(lixi));
           }
           System.out.println("还款总金额："+interest.multiply(new BigDecimal(year * 12)));
       }
    }

    public static void main(String[] args) {
        calcMortgage(new BigDecimal("600000"),new BigDecimal("0.0325"),30,2);
    }
}
