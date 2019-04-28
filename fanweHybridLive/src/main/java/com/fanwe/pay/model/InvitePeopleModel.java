package com.fanwe.pay.model;

import com.fanwe.hybrid.model.BaseActModel;

import java.util.List;

public class InvitePeopleModel extends BaseActModel {

    public String today_total = "0";
    public List<InvitePeopleBean> p_user_info;


    public List<InvitePeopleBean> getP_user_info() {
        return p_user_info;
    }

    public void setList(List<InvitePeopleBean> p_user_info) {
        this.p_user_info = p_user_info;
    }


    public String getToday_total() {
        return today_total;
    }

    public void setToday_total(String today_total) {
        this.today_total = today_total;
    }

    public static class InvitePeopleBean  {

        public String user_id;
        public String nick_name;
        public String chongzhi_total;
        public String diamonds_total;

        public String getUser_id() {
            return user_id;
        }

        public void setUser_id(String id) {
            this.user_id = id;
        }

        public String getNick_name() {
            return nick_name;
        }

        public void setNick_name(String name) {
            this.nick_name = name;
        }

        public String getChongzhi_total() {
            return chongzhi_total;
        }

        public void setChongzhi_total(String rechargeAccount) {
            this.chongzhi_total = rechargeAccount;
        }

        public String getDiamonds_total() {
            return diamonds_total;
        }

        public void setDiamonds_total(String consumptionAmount) {
            this.diamonds_total = consumptionAmount;
        }

        @Override
        public String toString() {
            return "InvitePeopleBean{" +
                    "user_id='" + user_id + '\'' +
                    ", nick_name='" + nick_name + '\'' +
                    ", chongzhi_total='" + chongzhi_total + '\'' +
                    ", diamonds_total='" + diamonds_total + '\'' +
                    '}';
        }
    }


}
