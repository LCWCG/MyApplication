package com.lcw.myapplication.util;

import android.content.Context;

import com.android.volley.Cache;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.cache.BitmapImageCache;
import com.android.volley.cache.SimpleImageLoader;
import com.android.volley.error.VolleyError;
import com.android.volley.request.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author 刘春旺
 *
 */
public class ServerApi {

    private static final String URL = "http://mkccf.haochedai.cn/?api&dykey=741d5cb7eb538d3651811a1e7c3e2a0b";
    /**
     * 首页广告——URL
     */
    private static final String POST_HOME_TOP_AD_URL = URL + "&module=article&q=getscrollpiclist";
    /**
     * 首页收益——URL
     */
    private static final String POST_HOME_LOAN_URL = URL + "&module=borrow&q=getborrowlist";
    /**
     * 注册——URL
     */
    private static final String POST_REGISTER_URL = URL + "&module=user&q=reg";
    /**
     * 登录——URL
     */
    private static final String POST_LOGIN_URL = URL + "&module=user&q=login";
    /**
     * 添加银行卡——URL
     */
    private static final String POST_ADD_BANK_URL = URL + "&module=user&q=card";
    /**
     * 获取银行卡列表——URL
     */
    private static final String POST_BANK_LIST_URL = URL + "&module=user&q=banklist";
    /**
     *获取用户信息——URL
     */
    private static final String POST_USER_INFO_URL = URL + "&module=user&q=getuser";
    /**
     * 银行卡设为默认——URL
     */
    private static final String POST_BANK_SET_DEFAULT = URL + "&module=user&q=carddefault";
    /**
     * 获取借款详情——URL
     */
    private static final String POST_LEND_MONEY_INFO_URL = URL + "&module=borrow&q=getborrowone";
    /**
     * 获取用户资金信息——URL
     */
    private static final String POST_USER_MONEY_INFO_URL = URL + "&module=user&q=getaccount";
    /**
     * 获取借款人资料、投资记录、审核记录——URL
     */
    private static final String POST_LEND_PERSON_INFO_URL = URL + "&module=borrow&q=getborrowrecord";
    /**
     * 标的合作机构——URL
     */
    private static final String POST_AGENCY_INFO_URL = URL + "&module=borrow&q=getagencyinfo";
    /**
     *用户投资——URL
     */
    private static final String POST_PAY = URL + "&module=user&q=tender";
    /**
     *身份认证——URL
     */
    private static final String POST_APPROVE_URL = URL + "&module=approve&q=realname";
    /**
     * 获取用户vip信息——URL
     */
    private static final String POST_IS_VIP_URL = URL + "&module=user&q=getvip";
    /**
     * 费用规则——URL
     */
    private static final String POST_COST_RULE_URL = URL + "&module=system&q=feiyong";
    /**
     * 提现——URL
     */
    private static final String POST_CASH_URL = URL + "&module=user&q=cash";
    /**
     * 提现列表——URL
     */
    private static final String POST_CASH_LIST_URL = URL + "&module=user&q=cashlist";
    /**
     *修改登录密码——URL
     */
    private static final String POST_ALTER_LOGIN_PASS_URL = URL + "&module=user&q=updatepassword";
    /**
     *修改支付密码——URL
     */
    private static final String POST_ALTER_PAY_PASS_URL = URL + "&module=user&q=updatepaypassword";

    public static final String IMG_URL = "https://www.haochedai.com/";

    private static RequestQueue sRequestQueue = null;
    private static SimpleImageLoader sImageLoader = null;

    public static void init(Context context) {
        sRequestQueue = Volley.newRequestQueue(context);
        sImageLoader = new SimpleImageLoader(sRequestQueue, BitmapImageCache.getInstance(null));
    }

    public static SimpleImageLoader getImageLoader() {
        return sImageLoader;
    }

    public static void getAlterPayPassData(final String oldpassword, final String newpassword, final Response.Listener<String> listener, final Response.ErrorListener errorListener ){
        Map<String, String> stringParams = new HashMap<String, String>();
        stringParams.put("user_id", UserInfo.getUid());
        stringParams.put("oldpassword", oldpassword);
        stringParams.put("newpassword", newpassword);
        StringRequest request = buildUpRequestNoCach(POST_ALTER_PAY_PASS_URL, stringParams, listener, errorListener);
        sRequestQueue.add(request);
    }

    public static void getAlterLoginPassData(final String oldpassword, final String newpassword, final Response.Listener<String> listener, final Response.ErrorListener errorListener ){
        Map<String, String> stringParams = new HashMap<String, String>();
        stringParams.put("user_id", UserInfo.getUid());
        stringParams.put("oldpassword", oldpassword);
        stringParams.put("newpassword", newpassword);
        StringRequest request = buildUpRequestNoCach(POST_ALTER_LOGIN_PASS_URL, stringParams, listener, errorListener);
        sRequestQueue.add(request);
    }

    //epage   每一行显示的条数（可以不传）
    //page    当前页面(可以不传)
    public static void getCashListData(final int epage, final int page,
                                   final Response.Listener<String> listener, final Response.ErrorListener errorListener ){
        Map<String, String> stringParams = new HashMap<String, String>();
        stringParams.put("user_id", UserInfo.getUid());
        stringParams.put("epage", String.valueOf(epage));
        stringParams.put("page", String.valueOf(page));
        StringRequest request = buildUpRequestNoCach(POST_CASH_LIST_URL, stringParams, listener, errorListener);
        sRequestQueue.add(request);
    }

    //3、安卓 4、ios
    public static void getCashData(final String paypassword, final String money, final String account,
                                   final Response.Listener<String> listener, final Response.ErrorListener errorListener ){
        Map<String, String> stringParams = new HashMap<String, String>();
        stringParams.put("user_id", UserInfo.getUid());
        stringParams.put("paypassword", paypassword);
        stringParams.put("money", money);
        stringParams.put("account", account);
        stringParams.put("source", "3");
        StringRequest request = buildUpRequestNoCach(POST_CASH_URL, stringParams, listener, errorListener);
        sRequestQueue.add(request);
    }

    public static void getCostRuleData(final Response.Listener<String> listener, final Response.ErrorListener errorListener ){
        Map<String, String> stringParams = new HashMap<String, String>();
        StringRequest request = buildUpRequestNoCach(POST_COST_RULE_URL, stringParams, listener, errorListener);
        sRequestQueue.add(request);
    }

    public static void getIsVIPData(final Response.Listener<String> listener, final Response.ErrorListener errorListener ){
        Map<String, String> stringParams = new HashMap<String, String>();
        stringParams.put("user_id", UserInfo.getUid());
        StringRequest request = buildUpRequestNoCach(POST_IS_VIP_URL, stringParams, listener, errorListener);
        sRequestQueue.add(request);
    }

    public static void getApproveData(final String card_id, final String realname,
                                      final Response.Listener<String> listener, final Response.ErrorListener errorListener ){
        Map<String, String> stringParams = new HashMap<String, String>();
        stringParams.put("user_id", UserInfo.getUid());
        stringParams.put("card_id", card_id);
        stringParams.put("realname", realname);
        StringRequest request = buildUpRequestNoCach(POST_APPROVE_URL, stringParams, listener, errorListener);
        sRequestQueue.add(request);
    }

    //contents  参数  安卓传1  IOS传2
    public static void getPayData(final String borrow_nid, final String paypassword, final String account,
                                  final String portion, final String borrow_password,
                                  final Response.Listener<String> listener, final Response.ErrorListener errorListener ){
        Map<String, String> stringParams = new HashMap<String, String>();
        stringParams.put("user_id", UserInfo.getUid());
        stringParams.put("borrow_nid", borrow_nid);
        stringParams.put("paypassword", paypassword);
        stringParams.put("account", account);

        if (portion != null) stringParams.put("portion", portion);
        stringParams.put("contents", "1");
        if (borrow_password != null) stringParams.put("borrow_password", borrow_password);
        StringRequest request = buildUpRequestNoCach(POST_PAY, stringParams, listener, errorListener);
        sRequestQueue.add(request);
    }

    public static void getAgencyInfoData(final String borrow_nid, final Response.Listener<String> listener, final Response.ErrorListener errorListener ){
        Map<String, String> stringParams = new HashMap<String, String>();
        stringParams.put("borrow_nid", borrow_nid);
        StringRequest request = buildUpRequestNoCach(POST_AGENCY_INFO_URL, stringParams, listener, errorListener);
        sRequestQueue.add(request);
    }

    public static void getLendPersonData(final String borrow_nid, final Response.Listener<String> listener, final Response.ErrorListener errorListener ){
        Map<String, String> stringParams = new HashMap<String, String>();
        stringParams.put("borrow_nid", borrow_nid);
        StringRequest request = buildUpRequestNoCach(POST_LEND_PERSON_INFO_URL, stringParams, listener, errorListener);
        sRequestQueue.add(request);
    }

    public static void getUserMoneyInfoData(final Response.Listener<String> listener, final Response.ErrorListener errorListener ){
        Map<String, String> stringParams = new HashMap<String, String>();
        stringParams.put("user_id", UserInfo.getUid());
        StringRequest request = buildUpRequestNoCach(POST_USER_MONEY_INFO_URL, stringParams, listener, errorListener);
        sRequestQueue.add(request);
    }

    public static void getLendMoneyData(final String borrow_nid, final Response.Listener<String> listener, final Response.ErrorListener errorListener ){
        Map<String, String> stringParams = new HashMap<String, String>();
        stringParams.put("borrow_nid", borrow_nid);
        StringRequest request = buildUpRequestNoCach(POST_LEND_MONEY_INFO_URL, stringParams, listener, errorListener);
        sRequestQueue.add(request);
    }

    public static void getBankDefaultData(final String id, final Response.Listener<String> listener, final Response.ErrorListener errorListener ){
        Map<String, String> stringParams = new HashMap<String, String>();
        stringParams.put("user_id", UserInfo.getUid());
        stringParams.put("id", id);
        StringRequest request = buildUpRequestNoCach(POST_BANK_SET_DEFAULT, stringParams, listener, errorListener);
        sRequestQueue.add(request);
    }

    public static void getUserInfoData(final Response.Listener<String> listener, final Response.ErrorListener errorListener ){
        Map<String, String> stringParams = new HashMap<String, String>();
        stringParams.put("user_id", UserInfo.getUid());
        StringRequest request = buildUpRequestNoCach(POST_USER_INFO_URL, stringParams, listener, errorListener);
        sRequestQueue.add(request);
    }

    public static void getBankListData(final Response.Listener<String> listener, final Response.ErrorListener errorListener ){
        Map<String, String> stringParams = new HashMap<String, String>();
        stringParams.put("user_id", UserInfo.getUid());
        StringRequest request = buildUpRequestNoCach(POST_BANK_LIST_URL, stringParams, listener, errorListener);
        sRequestQueue.add(request);
    }

    public static void getBankAddData(final String account, final Response.Listener<String> listener, final Response.ErrorListener errorListener ){
        Map<String, String> stringParams = new HashMap<String, String>();
        stringParams.put("user_id", UserInfo.getUid());
        stringParams.put("account", account);
        StringRequest request = buildUpRequestNoCach(POST_ADD_BANK_URL, stringParams, listener, errorListener);
        sRequestQueue.add(request);
    }

    public static void getLoginData(final String phone, final String password, final Response.Listener<String> listener, final Response.ErrorListener errorListener ){
        Map<String, String> stringParams = new HashMap<String, String>();
        stringParams.put("username", phone);
        stringParams.put("password", password);
        StringRequest request = buildUpRequestNoCach(POST_LOGIN_URL, stringParams, listener, errorListener);
        sRequestQueue.add(request);
    }

    public static void getRegisterData(final String phone, final String code, final String password,
                                       final Response.Listener<String> listener, final Response.ErrorListener errorListener ){
        Map<String, String> stringParams = new HashMap<String, String>();
        stringParams.put("phone", phone);
        if (code != null){
            stringParams.put("code", code);
        }
        if (password != null){
            stringParams.put("agent", "6");
            stringParams.put("password", password);
        }
        StringRequest request = buildUpRequestNoCach(POST_REGISTER_URL, stringParams, listener, errorListener);
        sRequestQueue.add(request);
    }

    //amount 显示条数
    //apptype android:1还是IOS:2
    public static void getHomeTopADData(final int amount,final int apptype , final Response.Listener<String> listener, final Response.ErrorListener errorListener ){
        Map<String, String> stringParams = new HashMap<String, String>();
        stringParams.put("amount", String.valueOf(amount));
        stringParams.put("apptype", String.valueOf(apptype));
        StringRequest request = buildUpRequest(POST_HOME_TOP_AD_URL, stringParams, listener, errorListener);
        sRequestQueue.add(request);
    }

    //status 值为1，防止直接访问用
    public static void getHomeLoanData(final int status, final Response.Listener<String> listener, final Response.ErrorListener errorListener ){
        Map<String, String> stringParams = new HashMap<String, String>();
        stringParams.put("status", String.valueOf(status));
        StringRequest request = buildUpRequest(POST_HOME_LOAN_URL, stringParams, listener, errorListener);
        sRequestQueue.add(request);
    }

    private static StringRequest buildUpRequest(final String url, final Map<String, String> params, final Response.Listener<String> listener, final Response.ErrorListener errorListener) {

        final StringRequest request = new StringRequest(Request.Method.POST, url, null, null);

        request.setListener(new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                listener.onResponse(response);
            }
        });

        request.setErrorListener(new Response.ErrorListener() {
            @Override public void onErrorResponse(VolleyError error) {
                if (request == null) {
                    //
                }
                //volley缓存--网络请求错误，查看本地缓存
                Cache.Entry entry = sRequestQueue.getCache().get(request.getCacheKey());
                if (entry != null) {
                    byte[] data = entry.data;
                    if (data != null) {
                        String response;
                        try {
                            response = new String(data, "UTF-8");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                            response = new String(data);
                        }
                        listener.onResponse(response);
                        return;
                    }
                }
                errorListener.onErrorResponse(error);
            }
        });

        request.setParams(params);
        request.setShouldCache(true);
        request.setForceRequest(true);

        return request;
    }

    private static StringRequest buildUpRequestNoCach(final String url, final Map<String, String> params, final Response.Listener<String> listener, final Response.ErrorListener errorListener) {

        final StringRequest request = new StringRequest(Request.Method.POST, url, null, null);

        request.setListener(new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                listener.onResponse(response);
            }
        });

        request.setErrorListener(new Response.ErrorListener() {
            @Override public void onErrorResponse(VolleyError error) {
                errorListener.onErrorResponse(error);
            }
        });

        request.setParams(params);
        request.setShouldCache(true);
        request.setForceRequest(true);

        return request;
    }

}
