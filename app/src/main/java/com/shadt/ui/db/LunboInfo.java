package com.shadt.ui.db;

import java.io.Serializable;
import java.util.List;

public class LunboInfo implements Serializable{

    /**
     * returnCode : 0
     * returnMsg : 调用成功
     * data : [{"id":13885,"uuid":"baf1b81c13a04229b3d0d74ee33d8c7e","img":"","img2":"","img3":"","title":"长大后再看小时候的课文，发现里面全是人生！","time":"2018-08-21 15:39:04 ","address":"","shopseat":"","product":"","iphone":"","score":"","context":"","oldcontext":"","RecordJumpUrl":"http://www.chinashadt.com:8050/syncott/Modile/newsServlet?uniqueID=1d75fd72bbaa4c28a97c163bf5908300&xgyd=newsOnCurrentEvents&createTime=2018-08-21","Traffic":1,"createType":"1"},{"id":13887,"uuid":"98a3f09f76cc43c7bf6f30301fe9dc84","img":"","img2":"","img3":"","title":"那些真正厉害的人，从来都在默默努力","time":"2018-08-21 15:40:38 ","address":"","shopseat":"","product":"","iphone":"","score":"","context":"","oldcontext":"","RecordJumpUrl":"http://www.chinashadt.com:8050/syncott/Modile/newsServlet?uniqueID=7249eafa94994d5d8e28124473313d7a&xgyd=newsOnCurrentEvents&createTime=2018-08-21","Traffic":0,"createType":"1"},{"id":13886,"uuid":"3a16e39faaaa4e43bb373e54ea69b6a5","img":"","img2":"","img3":"","title":"最好的关系，是遇事不责备","time":"2018-08-21 15:39:43 ","address":"","shopseat":"","product":"","iphone":"","score":"","context":"","oldcontext":"","RecordJumpUrl":"http://www.chinashadt.com:8050/syncott/Modile/newsServlet?uniqueID=dded99f5093244deb7cf8f8e8c8b391c&xgyd=newsOnCurrentEvents&createTime=2018-08-21","Traffic":1,"createType":"1"}]
     */

    private int returnCode;
    private String returnMsg;
    private List<DataBean> data;

    public int getReturnCode() {
        return returnCode;
    }

    public void setReturnCode(int returnCode) {
        this.returnCode = returnCode;
    }

    public String getReturnMsg() {
        return returnMsg;
    }

    public void setReturnMsg(String returnMsg) {
        this.returnMsg = returnMsg;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * id : 13885
         * uuid : baf1b81c13a04229b3d0d74ee33d8c7e
         * img :
         * img2 :
         * img3 :
         * title : 长大后再看小时候的课文，发现里面全是人生！
         * time : 2018-08-21 15:39:04
         * address :
         * shopseat :
         * product :
         * iphone :
         * score :
         * context :
         * oldcontext :
         * RecordJumpUrl : http://www.chinashadt.com:8050/syncott/Modile/newsServlet?uniqueID=1d75fd72bbaa4c28a97c163bf5908300&xgyd=newsOnCurrentEvents&createTime=2018-08-21
         * Traffic : 1
         * createType : 1
         */

        private int id;
        private String uuid;
        private String img;
        private String img2;
        private String img3;
        private String title;
        private String time;
        private String address;
        private String shopseat;
        private String product;
        private String iphone;
        private String score;
        private String context;
        private String oldcontext;
        private String RecordJumpUrl;
        private int Traffic;
        private String createType;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getUuid() {
            return uuid;
        }

        public void setUuid(String uuid) {
            this.uuid = uuid;
        }

        public String getImg() {
            return img;
        }

        public void setImg(String img) {
            this.img = img;
        }

        public String getImg2() {
            return img2;
        }

        public void setImg2(String img2) {
            this.img2 = img2;
        }

        public String getImg3() {
            return img3;
        }

        public void setImg3(String img3) {
            this.img3 = img3;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getShopseat() {
            return shopseat;
        }

        public void setShopseat(String shopseat) {
            this.shopseat = shopseat;
        }

        public String getProduct() {
            return product;
        }

        public void setProduct(String product) {
            this.product = product;
        }

        public String getIphone() {
            return iphone;
        }

        public void setIphone(String iphone) {
            this.iphone = iphone;
        }

        public String getScore() {
            return score;
        }

        public void setScore(String score) {
            this.score = score;
        }

        public String getContext() {
            return context;
        }

        public void setContext(String context) {
            this.context = context;
        }

        public String getOldcontext() {
            return oldcontext;
        }

        public void setOldcontext(String oldcontext) {
            this.oldcontext = oldcontext;
        }

        public String getRecordJumpUrl() {
            return RecordJumpUrl;
        }

        public void setRecordJumpUrl(String RecordJumpUrl) {
            this.RecordJumpUrl = RecordJumpUrl;
        }

        public int getTraffic() {
            return Traffic;
        }

        public void setTraffic(int Traffic) {
            this.Traffic = Traffic;
        }

        public String getCreateType() {
            return createType;
        }

        public void setCreateType(String createType) {
            this.createType = createType;
        }
    }
}
