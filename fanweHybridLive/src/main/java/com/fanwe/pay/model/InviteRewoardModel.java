package com.fanwe.pay.model;

import com.fanwe.hybrid.model.BaseActModel;

public class InviteRewoardModel extends BaseActModel {

        public String diamonds;
        public String money;
        public String inviteUrl;
        public String inviteNumber;
        public String inviteMoney;



        public String getDiamonds() {
            return diamonds;
        }

        public void setDiamonds(String diamonds) {
            this.diamonds = diamonds;
        }

        public String getMoney() {
            return money;
        }

        public void setMoney(String money) {
            this.money = money;
        }

        public String getInviteUrl() {
            return inviteUrl;
        }

        public void setInviteUrl(String inviteUrl) {
            this.inviteUrl = inviteUrl;
        }

        public String getInviteNumber() {
            return inviteNumber;
        }

        public void setInviteNumber(String inviteNumber) {
            this.inviteNumber = inviteNumber;
        }

        public String getInviteMoney() {
            return inviteMoney;
        }

        public void setInviteMoney(String inviteMoney) {
            this.inviteMoney = inviteMoney;
        }

        @Override
        public String toString() {
            return "InviteRewoardModel{" +
                    "diamonds='" + diamonds + '\'' +
                    ", money='" + money + '\'' +
                    ", inviteUrl='" + inviteUrl + '\'' +
                    ", inviteNumber='" + inviteNumber + '\'' +
                    ", inviteMoney='" + inviteMoney + '\'' +
                    '}';
        }


}
