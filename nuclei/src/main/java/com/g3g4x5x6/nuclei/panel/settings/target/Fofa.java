package com.g3g4x5x6.nuclei.panel.settings.target;

import com.g3g4x5x6.nuclei.ultils.Base64Utils;

public class Fofa {
    /**
     * 查询参数
     */
    // API URL接口
    protected String apiUrl;
    // 经过base64编码后的查询语法，即输入的查询内容
    protected String qbase64;
    // 可选字段，默认host,ip,port，详见附录1
    protected String fields;
    // 是否翻页，默认为第一页，按照更新时间排序
    protected String page;
    // 每页查询数量，默认为100条，最大支持10,000条/页
    protected String size;
    // 默认搜索一年内的数据，指定为true即可搜索全部数据
    protected String full;

    // 可选查询字段
    protected String FIELD_IP = "ip";                               // IP地址
    protected String FIELD_PORT = "port";                           // 端口
    protected String FIELD_PROTOCOL = "protocol";                   // 协议
    protected String FIELD_COUNTRY = "country";                     // 国家代码
    protected String FIELD_COUNTRY_NAME = "country_name";           // 国家名
    protected String FIELD_REGION = "region";                       // 区域
    protected String FIELD_CITY = "city";                           // 城市
    protected String FIELD_LONGITUDE = "longitude";                 // 纬度
    protected String FIELD_LATITUDE = "latitude";                   // 经度
    protected String FIELD_AS_NUMBER = "as_number";                 // asn编号
    protected String FIELD_AS_ORGANIZATION = "as_organization ";    // asn组织
    protected String FIELD_HOST = "host";                           // 主机名
    protected String FIELD_DOMAIN = "domain";                       // 域名
    protected String FIELD_OS = "os";                               // 操作系统
    protected String FIELD_SERVER = "server";                       // 网站server
    protected String FIELD_ICP = "icp";                             // icp备案号
    protected String FIELD_TITLE = "title";                         // 网站标题
    protected String FIELD_JARM = "jarm";                           // jarm指纹
    protected String FIELD_HEADER = "header";                       // 网站header
    protected String FIELD_BANNER = "banner";                       // 协议banner
    protected String FIELD_CERT = "cert";                           // 证书
    protected String FIELD_BODY = "body";                           // 网站正文内容
    protected String FIELD_FID = "fid";                             // 网站指纹信息
    protected String FIELD_STRUCTINFO = "structinfo";               // 结构化信息 (部分协议支持、比如elastic、mongodb)


    public String getApiUrl() {
        return apiUrl;
    }

    public void setApiUrl(String apiUrl) {
        this.apiUrl = apiUrl;
    }

    public String getQbase64() {
        return qbase64;
    }

    public void setQbase64(String qbase64) {
        this.qbase64 = Base64Utils.base64Encode(qbase64);
    }

    public String getFields() {
        return fields;
    }

    public void setFields(String fields) {
        this.fields = fields;
    }

    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getFull() {
        return full;
    }

    public void setFull(String full) {
        this.full = full;
    }
}
