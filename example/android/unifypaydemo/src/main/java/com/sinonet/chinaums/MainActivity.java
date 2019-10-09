package com.sinonet.chinaums;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.chinaums.pppay.unify.SocketFactory;
import com.chinaums.pppay.unify.UnifyMd5;
import com.chinaums.pppay.unify.UnifyPayListener;
import com.chinaums.pppay.unify.UnifyPayPlugin;
import com.chinaums.pppay.unify.UnifyPayRequest;
import com.sinonet.chinaums.model.PostonRequest;
import com.sinonet.chinaums.model.WXRequest;
import com.unionpay.UPPayAssistEx;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.KeyStore;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class MainActivity extends Activity implements UnifyPayListener {
    private final static String TAG = "MainActivity";
    private RadioGroup rgvalue;
    private RadioButton wxtype,zfbtype, alipay;
    private RadioGroup rgSecureTransaction;
    private int typetag = 0;
    private String mNotifyUrl = "http://172.16.26.178:8080/connectDemo/NotifyOperServlet";

    /**
     * 出账商户号
      */
    private ClearEditText mMerchantId;
    /**
     * 出账商户用户号
     */
    private ClearEditText mMerchantUserId;
    /**
     *  商户订单号
      */
    private ClearEditText mMerOrderId;
    /**
     * 交易金额
     */
    private ClearEditText mAmountText;

    /**
     * 消息来源
     */
//    private ClearEditText mMsgSrc;

    private ClearEditText mEditMsgSrc;
    private ClearEditText mEditSrcReserve;

    /**
     * 用户手机号
     */
    private ClearEditText mMobileId;
    private TextView mPayResult;
    private Spinner mSpinnerEnvironMent;
    private final int TYPE_POSTON = 0;
    private final int TYPE_WEIXIN = 1;
    private final int TYPE_ALIPAY = 2;
    /**
     * 云闪付
     */
    private final int TYPE_CLOUD_QUICK_PAY = 3;

    private final int ENV_TEST_ONE = 0;
    private final int ENV_TEST_TWO = 1;
    private final int ENV_NATIVE = 2;
    private final int ENV_PRODUCT = 3;
    /**
     * UAT支付环境
     */
    private final int ENV_ALIPAY_UAT = 4;

    private int mCurrentEnvironment = ENV_PRODUCT;

    private String mMd5SecretKey;
    private SharedPreferences mSharedPreferences;

    /**
     * 终端号
     */
    private ClearEditText mTerminerId;
    /**
     * 是否开启分帐复选框
     */
    private CheckBox cbDivision;
    /**
     * 分帐布局
     */
    private View layoutDivision;
    /**
     * 分帐平台商户分账金额
     */
    private ClearEditText mPlatformAmount;
    /**
     * 分帐子商户号
     */
    private ClearEditText mSubMid;
    /**
     * 分帐商户子订单号
     */
    private ClearEditText mSubMerOrderId;
    /**
     * 分帐子商户分账金额
     */
    private ClearEditText mSubTotalAmount;
    /**
     * 分帐添加分帐参数按钮
     */
    private View addDivisionInfo ;
    /**
     * 分帐参数展示
     */
    private ClearEditText divisionInfo;

    private Activity mActivity = null;

    String[] permissions = new String[]{Manifest.permission.READ_PHONE_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    List<String> mPermissionList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button button = (Button)findViewById(R.id.btn_order_pay);
        mSharedPreferences = getSharedPreferences("config", Context.MODE_PRIVATE);
        mMerchantId = (ClearEditText) findViewById(R.id.merchantId);
        mMerchantUserId = (ClearEditText) findViewById(R.id.merchantUserId);
        mMerOrderId = (ClearEditText) findViewById(R.id.merOrderID);
        mMerOrderId.setText(getOrderId());
        mAmountText = (ClearEditText) findViewById(R.id.amount);
        mMobileId = (ClearEditText) findViewById(R.id.mobileId);
        mPayResult = (TextView) findViewById(R.id.tv_callback);
        mTerminerId = (ClearEditText) findViewById(R.id.terminerid);
//        mMsgSrc = (ClearEditText) findViewById(R.id.msgsrc);
        mEditMsgSrc = (ClearEditText) findViewById(R.id.edit_msgsrc);
//        mEditMsgSrc.setSelection(0, true);
        mEditSrcReserve = (ClearEditText) findViewById(R.id.edit_src_reserve);
        //支付方式选择
        rgvalue=(RadioGroup)findViewById(R.id.radio_group);
        wxtype=(RadioButton)findViewById(R.id.weixin_pay);
        zfbtype=(RadioButton)findViewById(R.id.poston_pay);
        alipay = (RadioButton) findViewById(R.id.alibaba_pay);
        rgvalue.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // TODO Auto-generated method stub
                if(mCurrentEnvironment == ENV_ALIPAY_UAT && checkedId != alipay.getId()){
                    group.check(alipay.getId());
                    Toast.makeText(getApplicationContext(),"UAT环境只支持支付宝",Toast.LENGTH_LONG).show();
                    return;
                }

                if (wxtype.getId() == checkedId) {
                    typetag = 1;//微信
                }
                if (zfbtype.getId() == checkedId) {
                    typetag = 0;//poston
                }
                if(alipay.getId() == checkedId){
                    typetag = 2;//zhifubao
                }
                if(checkedId == R.id.cloud_quick_pay){
                    typetag = TYPE_CLOUD_QUICK_PAY;//云闪付;
                }
                switchParam(typetag, mCurrentEnvironment);
            }
        });
        rgSecureTransaction = (RadioGroup) findViewById(R.id.radio_group_secure_transaction);
        mSpinnerEnvironMent = (Spinner) findViewById(R.id.spinner_environment);
        mSpinnerEnvironMent.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            private int lastPostion;
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int postion, long l) {
                switch (postion){
                    case 0:
                        mCurrentEnvironment = ENV_PRODUCT;
                       break;
                    case 1:
                        mCurrentEnvironment = ENV_TEST_ONE;
                        break;
                    case 2:
                        mCurrentEnvironment = ENV_TEST_TWO;
                        break;
                    case 3:
                        if(typetag != TYPE_ALIPAY){
                            mSpinnerEnvironMent.setSelection(lastPostion,true);
                            Toast.makeText(getApplicationContext(),"UAT环境只支持支付宝",Toast.LENGTH_LONG).show();
                            return;
                        }

                        mCurrentEnvironment = ENV_ALIPAY_UAT;
                        break;
                }
                lastPostion = postion;
                switchParam(typetag, mCurrentEnvironment);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        mSpinnerEnvironMent.setSelection(0, true);
        initDivisionView();
        mCurrentEnvironment = ENV_PRODUCT;
        switchParam(typetag, mCurrentEnvironment);

        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new GetPrepayIdTask().execute();
            }
        });

        UnifyPayPlugin.getInstance(this).setListener(this);
        mActivity = this;
       //权限确认
        mPermissionList.clear();
        for (int i = 0; i < permissions.length; i++) {
            if (ContextCompat.checkSelfPermission(this, permissions[i]) != PackageManager.PERMISSION_GRANTED) {
                mPermissionList.add(permissions[i]);
            }
        }

        if (!mPermissionList.isEmpty()) {
            //请求权限方法
            String[] permissions = mPermissionList.toArray(new String[mPermissionList.size()]);//将List转为数组
            ActivityCompat.requestPermissions(this, permissions, 1);
        }
    }

    /**
     * 分帐相关信息集合
     */
    JSONArray divisionInfosArray = new JSONArray();
    /**
     * 分帐相关信息初始化
     */
    void initDivisionView(){
        mPlatformAmount= (ClearEditText) findViewById(R.id.platform_amount);
        mSubMid= (ClearEditText) findViewById(R.id.sub_mid);
        mSubMerOrderId=(ClearEditText) findViewById(R.id.sub_mer_orderId);
        mSubTotalAmount=(ClearEditText) findViewById(R.id.sub_total_amount);
        cbDivision = (CheckBox) findViewById(R.id.cb_division);
        layoutDivision = findViewById(R.id.layout_division);
        divisionInfo = (ClearEditText) findViewById(R.id.et_division_info);
        addDivisionInfo = findViewById(R.id.bt_add_division_info);
        addDivisionInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(mSubMerOrderId.getText())&&TextUtils.isEmpty(mSubMid.getText())
                        &&TextUtils.isEmpty(mSubTotalAmount.getText())){
                    Toast.makeText(getApplication(),"商户分帐子信息不能为空",Toast.LENGTH_SHORT).show();
                    return;
                }

                JSONObject object = new JSONObject();
                try {
                    object.put("mid",mSubMid.getText());
                    object.put("merOrderId",mSubMerOrderId.getText());
                    object.put("totalAmount",mSubTotalAmount.getText());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                divisionInfosArray.put(object);
                divisionInfo.setText(divisionInfosArray.toString());
            }
        });
        cbDivision.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if(checked){
                    layoutDivision.setVisibility(compoundButton.VISIBLE);
                }else {
                    layoutDivision.setVisibility(compoundButton.GONE);
                }
            }
        });
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        // 判断是否勾选禁止后不再询问
                        boolean showRequestPermission = ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[i]);
                        if (showRequestPermission) {//
                            //judgePermission();//重新申请权限
                            return;
                        } else {
                            finish();                        }
                    }
                }
                break;
            default:
                break;
        }
    }

    private void switchParam(int type, int currentEnvironment){
        Log.i(TAG, "type="+type+", currentEnvironment="+currentEnvironment);
        if (type == TYPE_POSTON) {
            switch (currentEnvironment) {
                case ENV_TEST_ONE:
                case ENV_TEST_TWO:
                case ENV_NATIVE:
                    mEditMsgSrc.setText("ERP_SCANPAY");
                    mMerchantId.setText("898460107800170");
                    mTerminerId.setText("00000170");
                    mMerOrderId.setText(getPostOrderId());
                    mMd5SecretKey = "EahB2xfpCCpaYtKw2yCWzcTfChTxXEYKCGwBEaMcDKbEHCpE";
                    break;
                case ENV_PRODUCT:
                    mEditMsgSrc.setText("ERP_SCANPAY");
                    mMerchantId.setText("898310058124024");
                    mTerminerId.setText("00000001");
                    mMerOrderId.setText(getPostOrderId());
                    mMd5SecretKey = "3ypmTzxdXhFty7HCrZynehjcjdcaAb3HDRwJQpTFYZfjWHEZ";
                    break;
            }
        } else if (type == TYPE_WEIXIN) {
            switch (currentEnvironment) {
                case ENV_TEST_ONE:
                    mEditMsgSrc.setText("ERP_SCANPAY");
                    mMerchantId.setText("898310052114003");//898310060514001
                    mTerminerId.setText("00000001");//"12345678"
                    mMerOrderId.setText(getCommonOrder("3028"));//"3194"//getProOrderId()
                    mSubMerOrderId.setText(getCommonOrder("3028"));
                    mMd5SecretKey = "EahB2xfpCCpaYtKw2yCWzcTfChTxXEYKCGwBEaMcDKbEHCpE";//"1234567890lkkjjhhguuijmjfidfi4urjrjmu4i84jvm";
                    break;
                case ENV_TEST_TWO:
                    mEditMsgSrc.setText("WWW.TEST.COM");//"NETPAY"
                    mMerchantId.setText("898310052114003");//898310060514001
                    mTerminerId.setText("00000001");//"12345678"
                    mMerOrderId.setText(getCommonOrder("3194"));//getProOrderId()
                    mSubMerOrderId.setText(getCommonOrder("3194"));
                    mMd5SecretKey = "fcAmtnx7MwismjWNhNKdHC44mNXtnEQeJkRrhKJwyrW2ysRR";//"1234567890lkkjjhhguuijmjfidfi4urjrjmu4i84jvm";
                    break;
                case ENV_NATIVE:
                    mEditMsgSrc.setText("ERP_SCANPAY");
                    mMerchantId.setText("898310060514001");
                    mTerminerId.setText("88880001");
                    mMerOrderId.setText(getPostOrderId());
                    mMd5SecretKey = "EahB2xfpCCpaYtKw2yCWzcTfChTxXEYKCGwBEaMcDKbEHCpE";
                    break;
                case ENV_PRODUCT:
                    mEditMsgSrc.setText("NETPAYTEST");
                    mMerchantId.setText("898310173992528");//898310060514001
                    mTerminerId.setText("70162265");//"12345678"
                    mMerOrderId.setText(getCommonOrder("1028"));//getProOrderId()
                    mSubMerOrderId.setText(getCommonOrder("1028"));
                    mMd5SecretKey = "BcNys5ix3zj4TTSz8HhrXWrZJZHWJBXzMSXdNWxPZ6B7JasS";
                    break;
            }
        } else if (type == TYPE_ALIPAY) {
            switch (currentEnvironment) {
                case ENV_NATIVE:
                case ENV_TEST_ONE:
                    mEditMsgSrc.setText("NETPAY_DEMO");
                    mMerchantId.setText("898310058124024");
                    mTerminerId.setText("12345678");
                    mMerOrderId.setText(getCommonOrder("1028"));// getPostOrderId()
                    mMd5SecretKey = "dwpRz2B6akcp8fwp6JJjenHCH7FKHFcCPE3NkiMJAQzhtD3W";
                    break;
                case ENV_TEST_TWO:
                    mEditMsgSrc.setText("NETPAY");
                    mMerchantId.setText("898310060514001");
                    mTerminerId.setText("12345678");
                    mMerOrderId.setText(getProOrderId());
                    mMd5SecretKey = "1234567890lkkjjhhguuijmjfidfi4urjrjmu4i84jvm";
                    break;
                case ENV_PRODUCT:
                    mEditMsgSrc.setText("WWW.PRODTEST.COM");
                    mMerchantId.setText("898310058124024");
                    mTerminerId.setText("12345678");
                    mMerOrderId.setText(getCommonOrder("5000"));
                    mMd5SecretKey = "AcZdi46z6GibDwi5WXQEdypEWt2WSdNH6RHT3YAwnmCWwQEG";
                    break;
                case ENV_ALIPAY_UAT:
                    mEditMsgSrc.setText("WWW.TEST.COM");
                    mMerchantId.setText("888888800004545");
                    mTerminerId.setText("88889999");
                    mMerOrderId.setText(getCommonOrder("3194"));
                    mMd5SecretKey = "fcAmtnx7MwismjWNhNKdHC44mNXtnEQeJkRrhKJwyrW2ysRR";
                    break;
            }
        } else if (type == TYPE_CLOUD_QUICK_PAY) {
            switch (currentEnvironment) {
                case ENV_PRODUCT:
                    mEditMsgSrc.setText("WWW.PRODTEST.COM");
                    mMerchantId.setText("898310148160568");
                    mTerminerId.setText("12345678");
                    mMerOrderId.setText(getCommonOrder("5000"));
                    mMd5SecretKey = "AcZdi46z6GibDwi5WXQEdypEWt2WSdNH6RHT3YAwnmCWwQEG";
                    break;
                case ENV_NATIVE:
                case ENV_TEST_ONE:
                    mEditMsgSrc.setText("NETPAY");
                    mMerchantId.setText("898310148160568");
                    mTerminerId.setText("12345678");
                    mMerOrderId.setText(getPostOrderId());
                    mMd5SecretKey = "1234567890lkkjjhhguuijmjfidfi4urjrjmu4i84jvm";
                    break;
                case ENV_TEST_TWO:
                    break;
            }
        }
        Log.d(TAG,"mMd5SecretKey = " + mMd5SecretKey);
    }

    private String getOrderId(){
        Random random = new Random();
        StringBuilder sb = new StringBuilder("3028"); //weixin
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        sb.append(df.format(new Date()));
        for(int i=0; i < 7; i++){
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }
    private String getOrderId4Weixin(){
        Random random = new Random();
        StringBuilder sb = new StringBuilder("3816"); //weixin 3028
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        sb.append(df.format(new Date()));
        for(int i=0; i < 7; i++){
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }
    private String getPostOrderId(){
        Random random = new Random();
        StringBuilder sb = new StringBuilder("3028"); //weixin
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        sb.append(df.format(new Date()));
        for(int i=0; i < 7; i++){
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }

    private String getProOrderId(){
        Random random = new Random();
        StringBuilder sb = new StringBuilder("3245"); //支付宝、微信生产环境
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        sb.append(df.format(new Date()));
        for(int i=0; i < 7; i++){
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }

    private String getCommonOrder(String preFix){
        Random random = new Random();
        StringBuilder sb = new StringBuilder(preFix); //支付宝、微信生产环境
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        sb.append(df.format(new Date()));
        for(int i=0; i < 7; i++){
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }

    @Override
    public void onResult(String resultCode, String resultInfo) {
        Log.d(TAG, "onResult resultCode="+resultCode+", resultInfo="+resultInfo);
    }

    private class GetPrepayIdTask extends AsyncTask<Void, Void, String>
    {

        private ProgressDialog dialog;

        public GetPrepayIdTask()
        {
        }

        @Override
        protected void onPreExecute()
        {
            dialog =
                    ProgressDialog.show(MainActivity.this,
                            getString(R.string.app_tip),
                            getString(R.string.getting_prepayid));
        }

        @Override
        protected void onPostExecute(String result)
        {

            ApplicationInfo appInfo = null;
            try {
                appInfo = mActivity.getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }

            if (dialog != null){
                dialog.dismiss();
            }

            if (result == null){
                Toast.makeText(MainActivity.this, getString(R.string.get_prepayid_fail, "network connect error"), Toast.LENGTH_LONG).show();
                mPayResult.setText(getString(R.string.mpos_callback) + "network connect error");
            }
            else
            {
                Log.i(TAG, "onPostExecute-->" + result);
                try {
                    JSONObject json = new JSONObject(result);
                    String status = json.getString("errCode");
                    if (status.equalsIgnoreCase("SUCCESS")) // 成功
                    {

                        Log.e(TAG, "appPayRequest=" + json.getString("appPayRequest"));
                        if (json.isNull("appPayRequest")) {
                            Toast.makeText(MainActivity.this, "服务器返回数据格式有问题，缺少“appPayRequest”字段", Toast.LENGTH_LONG).show();
                            return;
                        } else {
                            Toast.makeText(MainActivity.this, R.string.get_prepayid_succ, Toast.LENGTH_LONG).show();
                        }

                        if (typetag == 0) {
                            payUMSPay(json.getString("appPayRequest"));
                        } else if (typetag == 1) {
                            payWX(json.getString("appPayRequest"));
                        } else if (typetag == 2) {
                            payAliPay(json.getString("appPayRequest"));
                        } else if (typetag == 3) {
                            payCloudQuickPay(json.getString("appPayRequest"));
                        }
                    } else {
                        String msg = String.format(getString(R.string.get_prepayid_fail),json.getString("errMsg"));
                        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_LONG)
                                .show();
                        mPayResult.setText(getString(R.string.mpos_callback) + msg);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }

        @Override
        protected void onCancelled()
        {
            super.onCancelled();
        }

        @Override
        protected String doInBackground(Void... params) {
            String url = "https://qr.chinaums.com/netpay-route-server/api/";
            if (mCurrentEnvironment == 0) {//测试一环境
                url = "https://qr-test1.chinaums.com/netpay-route-server/api/";//"https://qr-test1.chinaums.com/netpay-portal/test/tradeTest.do";//
            }else if(mCurrentEnvironment == 1){//测试二环境
                url = "https://qr-test2.chinaums.com/netpay-route-server/api/";//"http://umspay.izhong.me/netpay-route-server/api/";
            }else if(mCurrentEnvironment == 2  && typetag != 0){
                url = "https://mobl-test.chinaums.com/netpay-route-server/api/";
            }else if(mCurrentEnvironment == 3){
                url = "https://qr.chinaums.com/netpay-route-server/api/";
            }else if(typetag == 0 && mCurrentEnvironment == 2){
                url = "https://qr-test1.chinaums.com/netpay-route-server/api/";
            }

            if(typetag == TYPE_WEIXIN && mCurrentEnvironment == 0){
                url = "https://qr-test3.chinaums.com/netpay-route-server/api/";//url = "https://mobl-test.chinaums.com/netpay-route-server/api/";
            }
            //2018-07-11
            if(typetag == TYPE_ALIPAY && mCurrentEnvironment == ENV_ALIPAY_UAT){
                url = "https://qr-test5.chinaums.com/netpay-route-server/api/";
            }

            String entity = null;
            Log.d(TAG, "typetag:" + typetag);
            if(typetag == 1){
                divisionInfosArray = new JSONArray();
                entity = getWeiXinParams();
            }else if(typetag == 0){
                entity = getPostParam();
            }else if(typetag == 2){
                if(mCurrentEnvironment == ENV_ALIPAY_UAT){
                    entity = getAliPayUatParm();
                }else {
                    entity = getAliPayParm();
                }
            }else if(typetag == 3){
                entity = getCloudQuickPayParm();
            }

            Log.d(TAG, "doInBackground, url = " + url);
            Log.d(TAG, "doInBackground, entity = " + entity);


            byte[] buf = httpPost(url, entity);
            if (buf == null || buf.length == 0)
            {
                return null;
            }
            String content = new String(buf);
            Log.d(TAG, "doInBackground, content = " + content);
//            result.parseFrom(content);
            try
            {
                return content;
            }
            catch (Exception e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
                Log.d(TAG, "doInBackground, Exception = " + e.getMessage());
                return null;
            }
        }
    }

    /**
     * 微信
     * @param parms
     */
    private void payWX(String parms){
        UnifyPayRequest msg = new UnifyPayRequest();
        msg.payChannel = UnifyPayRequest.CHANNEL_WEIXIN;
        msg.payData = parms;
        UnifyPayPlugin.getInstance(this).sendPayRequest(msg);
    }

    /**
     * 支付宝
     * @param parms
     */
    private void payAliPay(String parms){
        UnifyPayRequest msg = new UnifyPayRequest();
        msg.payChannel = UnifyPayRequest.CHANNEL_ALIPAY;
        msg.payData = parms;
        UnifyPayPlugin.getInstance(this).sendPayRequest(msg);
    }

    /**
     * 快捷支付
     * @param parms
     */
    private void payUMSPay(String parms){
        UnifyPayRequest msg = new UnifyPayRequest();
        msg.payChannel = UnifyPayRequest.CHANNEL_UMSPAY;
        msg.payData = parms;
        UnifyPayPlugin.getInstance(this).sendPayRequest(msg);
    }

    /**
     * 云闪付
     * @param appPayRequest
     */
    private void payCloudQuickPay(String appPayRequest) {
        String tn = "空";
        try {
            JSONObject e = new JSONObject(appPayRequest);
            tn = e.getString("tn");
        } catch (JSONException e1) {
            e1.printStackTrace();
        }
        UPPayAssistEx.startPay (this, null, null, tn, "00");
        Log.d("test","云闪付支付 tn = " + tn);
    }
    /**
     * 组装参数
     * <功能详细描述>
     * @return
     * @see [类、类#方法、类#成员]
     */
    private String getWeiXinParams()
    {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Map<String, String> params = new HashMap<String, String>();
        String orderId = getCommonOrder("3194");//getOrderId4Weixin();
        params.put("instMid", "APPDEFAULT");
        params.put("merOrderId", mMerOrderId.getText().toString());
        params.put("mid", mMerchantId.getText().toString());
        params.put("msgId","dsa2231s");
        params.put("msgSrc", mEditMsgSrc.getText().toString());//"WWW.SHHXQWLKJ.COM"//
        params.put("msgType", "wx.appPreOrder"); //"wx.unifiedOrder"// 机器ip地址
        params.put("requestTimestamp",sdf.format(new Date()));
        params.put("tid", mTerminerId.getText().toString());
        params.put("totalAmount", mAmountText.getEditableText().toString());
        params.put("tradeType", "APP");
        params.put("subAppId", "wxc71b9ae0235a4c30");
        params.put("secureTransaction",String.valueOf(rgSecureTransaction.getCheckedRadioButtonId() == R.id.secure_transaction_true));
        params.put("srcReserve", mEditSrcReserve.getText().toString());//"商户想定制化展示的内容，长度不大于255"
        params.put("divisionFlag", cbDivision.isChecked()+"");
        //if(cbDivision.isChecked()){
        params.put("platformAmount", cbDivision.isChecked()?mPlatformAmount.getText().toString():"");
        params.put("subOrders", cbDivision.isChecked()?divisionInfo.getText().toString():"[]");
        //}
        String sign = signWithMd5(buildSignString(params),mMd5SecretKey,"UTF-8");//signWithMd5(buildSignString(params),"fcAmtnx7MwismjWNhNKdHC44mNXtnEQeJkRrhKJwyrW2ysRR","UTF-8");//"fZjyfDK7ix7CKhhBSC8mQWTAtmp44JsTrbkkyKXtxNAxxPFT"//
        params.put("sign", sign);

        WXRequest req = new WXRequest();
        req.tid = params.get("tid");//mTerminerId.getText().toString();
        req.msgSrc = params.get("msgSrc");//mEditMsgSrc.getText().toString();;
        req.requestTimestamp = params.get("requestTimestamp");
        req.merOrderId = params.get("merOrderId");
        req.mid =  params.get("mid");//mMerchantId.getText().toString();
        req.msgType = params.get("msgType");//"wx.unifiedOrder";
        req.msgId = "dsa2231s";
        req.totalAmount = mAmountText.getEditableText().toString();
        req.instMid = "APPDEFAULT";
        req.tradeType = "APP";
        req.subAppId = "wxc71b9ae0235a4c30";
        req.sign = sign;
        req.secureTransaction = params.get("secureTransaction");
        req.srcReserve = params.get("srcReserve");
        req.divisionFlag = params.get("divisionFlag");
        //if(cbDivision.isChecked()){
        req.platformAmount = params.get("platformAmount");
        req.subOrders = params.get("subOrders");
        // }
        return req.toString();
    }

    private String getAliPayParm(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Map<String, String> params = new HashMap<String, String>();
        params.put("tid", mTerminerId.getText().toString());
        params.put("msgSrc", mEditMsgSrc.getText().toString());
        params.put("requestTimestamp",sdf.format(new Date()));
        params.put("merOrderId", mMerOrderId.getText().toString());
        params.put("totalAmount", mAmountText.getEditableText().toString());
        params.put("mid", mMerchantId.getText().toString());
        params.put("msgType", "trade.precreate"); // 机器ip地址
        params.put("instMid", "APPDEFAULT");
        params.put("mobile", mMobileId.getText().toString());
        params.put("msgId", getPostOrderId());
        params.put("orderSource", "NETPAY");
        params.put("merchantUserId", mMerchantUserId.getText().toString());//"898340149000005"
        params.put("secureTransaction",String.valueOf(rgSecureTransaction.getCheckedRadioButtonId() == R.id.secure_transaction_true));
        params.put("srcReserve", mEditSrcReserve.getText().toString());//"商户想定制化展示的内容，长度不大于255"
        String sign = signWithMd5(buildSignString(params),mMd5SecretKey,"UTF-8");//signWithMd5(buildSignString(params),"fcAmtnx7MwismjWNhNKdHC44mNXtnEQeJkRrhKJwyrW2ysRR","UTF-8");//
        params.put("sign", sign);
        PostonRequest req = new PostonRequest();
        req.tid = params.get("tid");
        req.msgSrc = params.get("msgSrc");
        req.requestTimestamp = params.get("requestTimestamp");
        req.merOrderId =  params.get("merOrderId");
        req.totalAmount = params.get("totalAmount");
        req.mid = params.get("mid");
        req.msgType = params.get("msgType");
        req.instMid = params.get("instMid");
        req.mobile = params.get("mobile");
        req.msgId = params.get("msgId");
        req.orderSource = params.get("orderSource");
        req.merchantUserId = params.get("merchantUserId");
        req.sign = sign;
        req.secureTransaction = params.get("secureTransaction");
        req.srcReserve = params.get("srcReserve");
        return req.toString();
    }

    /**
     * UAT环境（暂只用于支付宝支付方式）2018-07-11
     * @return
     */
    private String getAliPayUatParm(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Map<String, String> params = new HashMap<String, String>();
        params.put("tid", mTerminerId.getText().toString());
        params.put("msgSrc", mEditMsgSrc.getText().toString());
        params.put("requestTimestamp",sdf.format(new Date()));
        params.put("merOrderId", mMerOrderId.getText().toString());
        params.put("totalAmount", mAmountText.getEditableText().toString());
        params.put("mid", mMerchantId.getText().toString());
        params.put("msgType", "trade.precreate"); // 机器ip地址
        params.put("instMid", "APPDEFAULT");
        params.put("mobile", mMobileId.getText().toString());
        params.put("msgId", getPostOrderId());
        params.put("orderSource", "NETPAY");
        params.put("merchantUserId", mMerchantUserId.getText().toString());//"898340149000005"
        params.put("secureTransaction",String.valueOf(rgSecureTransaction.getCheckedRadioButtonId() == R.id.secure_transaction_true));
        params.put("srcReserve", mEditSrcReserve.getText().toString());//"商户想定制化展示的内容，长度不大于255"
        String sign = signWithMd5(buildSignString(params),mMd5SecretKey,"UTF-8");//signWithMd5(buildSignString(params),"fcAmtnx7MwismjWNhNKdHC44mNXtnEQeJkRrhKJwyrW2ysRR","UTF-8");//
        params.put("sign", sign);
        PostonRequest req = new PostonRequest();
        req.tid = params.get("tid");
        req.msgSrc = params.get("msgSrc");
        req.requestTimestamp = params.get("requestTimestamp");
        req.merOrderId =  params.get("merOrderId");
        req.totalAmount = params.get("totalAmount");
        req.mid = params.get("mid");
        req.msgType = params.get("msgType");
        req.instMid = params.get("instMid");
        req.mobile = params.get("mobile");
        req.msgId = params.get("msgId");
        req.orderSource = params.get("orderSource");
        req.merchantUserId = params.get("merchantUserId");
        req.sign = sign;
        req.secureTransaction = params.get("secureTransaction");
        req.srcReserve = params.get("srcReserve");
        return req.toString();
    }

    private String getPostParam(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Map<String, String> params = new HashMap<String, String>();
        String orderId = getPostOrderId();
        params.put("tid", mTerminerId.getText().toString());
        params.put("msgSrc", mEditMsgSrc.getText().toString());
        params.put("requestTimestamp",sdf.format(new Date()));
        params.put("merOrderId", mMerOrderId.getText().toString());
        params.put("totalAmount", mAmountText.getEditableText().toString());
        params.put("mid", mMerchantId.getText().toString());
        params.put("msgType", "qmf.order"); // 机器ip地址
        params.put("instMid", "APPDEFAULT");
        params.put("mobile", mMobileId.getText().toString());
        params.put("msgId", getPostOrderId());
        params.put("orderSource", "NETPAY");
        params.put("merchantUserId", mMerchantUserId.getText().toString());
        params.put("secureTransaction",String.valueOf(rgSecureTransaction.getCheckedRadioButtonId() == R.id.secure_transaction_true));
        params.put("srcReserve", mEditSrcReserve.getText().toString());//"商户想定制化展示的内容，长度不大于255"
        String sign = signWithMd5(buildSignString(params),mMd5SecretKey,"UTF-8");
        params.put("sign", sign);
        PostonRequest req = new PostonRequest();
        req.tid = params.get("tid");
        req.msgSrc = params.get("msgSrc");
        req.requestTimestamp = params.get("requestTimestamp");
        req.merOrderId =  params.get("merOrderId");
        req.totalAmount = params.get("totalAmount");
        req.mid = params.get("mid");
        req.msgType = params.get("msgType");
        req.instMid = params.get("instMid");
        req.mobile = params.get("mobile");
        req.msgId = params.get("msgId");
        req.orderSource = params.get("orderSource");
        req.merchantUserId = params.get("merchantUserId");
        req.sign = sign;
        req.secureTransaction = params.get("secureTransaction");
        req.srcReserve = params.get("srcReserve");
        return req.toString();
    }

    /**
     * 云闪付
     * @return
     */
    private String getCloudQuickPayParm1() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Map<String, String> params = new HashMap<String, String>();
        String orderId = getPostOrderId();
        params.put("tid", mTerminerId.getText().toString());
        params.put("msgSrc", "NETPAY");//mEditMsgSrc.getText().toString()
        params.put("requestTimestamp",sdf.format(new Date()));
        params.put("merOrderId", mMerOrderId.getText().toString());
        params.put("totalAmount", mAmountText.getEditableText().toString());
        params.put("mid", "898310148160568");//mMerchantId.getText().toString()
        params.put("msgType", "uac.appOrder"); // 机器ip地址
        params.put("instMid", "APPDEFAULT");
        params.put("mobile", mMobileId.getText().toString());
        params.put("msgId", getPostOrderId());
        params.put("orderSource", "NETPAY");
        params.put("merchantUserId", mMerchantUserId.getText().toString());
        params.put("secureTransaction",String.valueOf(rgSecureTransaction.getCheckedRadioButtonId() == R.id.secure_transaction_true));
        String sign = signWithMd5(buildSignString(params),"1234567890lkkjjhhguuijmjfidfi4urjrjmu4i84jvm","UTF-8");//signWithMd5(buildSignString(params),mMd5SecretKey,"UTF-8");
        params.put("sign", sign);
        PostonRequest req = new PostonRequest();
        req.tid = params.get("tid");
        req.msgSrc = params.get("msgSrc");
        req.requestTimestamp = params.get("requestTimestamp");
        req.merOrderId =  params.get("merOrderId");
        req.totalAmount = params.get("totalAmount");
        req.mid = params.get("mid");
        req.msgType = params.get("msgType");
        req.instMid = params.get("instMid");
        req.mobile = params.get("mobile");
        req.msgId = params.get("msgId");
        req.orderSource = params.get("orderSource");
        req.merchantUserId = params.get("merchantUserId");
        req.sign = sign;
        req.secureTransaction = params.get("secureTransaction");
        return req.toString();
    }
    /**
     * 云闪付
     * @return
     */
    private String getCloudQuickPayParm(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Map<String, String> params = new HashMap<String, String>();
        String orderId = getOrderId();
        params.put("instMid", "APPDEFAULT");
        params.put("merOrderId", mMerOrderId.getText().toString());
        params.put("mid", mMerchantId.getText().toString());////"898310148160568"
        params.put("msgId","dsa2231s");
        params.put("msgSrc", mEditMsgSrc.getText().toString());//"NETPAY"
        params.put("msgType", "uac.appOrder");//"wx.unifiedOrder"// 机器ip地址
        params.put("requestTimestamp",sdf.format(new Date()));
        params.put("tid", mTerminerId.getText().toString());
        params.put("totalAmount", mAmountText.getEditableText().toString());
        params.put("tradeType", "APP");
        params.put("subAppId", "wxc71b9ae0235a4c30");
        params.put("secureTransaction",String.valueOf(rgSecureTransaction.getCheckedRadioButtonId() == R.id.secure_transaction_true));
        params.put("srcReserve", mEditSrcReserve.getText().toString());
        params.put("divisionFlag", cbDivision.isChecked()+"");
        //if(cbDivision.isChecked()){
        params.put("platformAmount", cbDivision.isChecked()?mPlatformAmount.getText().toString():"");
        params.put("subOrders", cbDivision.isChecked()?divisionInfo.getText().toString():"[]");
        //}
        String sign = signWithMd5(buildSignString(params),mMd5SecretKey,"UTF-8");//signWithMd5(buildSignString(params),"1234567890lkkjjhhguuijmjfidfi4urjrjmu4i84jvm","UTF-8");//
        params.put("sign", sign);

        WXRequest req = new WXRequest();
        req.tid = mTerminerId.getText().toString();
        req.msgSrc = mEditMsgSrc.getText().toString();// "NETPAY";
        req.requestTimestamp = params.get("requestTimestamp");
        req.merOrderId = params.get("merOrderId");
        req.mid =  params.get("mid");//mMerchantId.getText().toString();
        req.msgType = params.get("msgType");//"wx.unifiedOrder";
        req.msgId = params.get("msgId");//"dsa2231s";
        req.totalAmount = mAmountText.getEditableText().toString();
        req.instMid = params.get("instMid");//"APPDEFAULT";
        req.tradeType = params.get("tradeType");//"APP";
        req.subAppId = params.get("subAppId");//"wxc71b9ae0235a4c30";
        req.sign = sign;
        req.secureTransaction = params.get("secureTransaction");
        req.srcReserve = params.get("srcReserve");
        req.divisionFlag = params.get("divisionFlag");
        //if(cbDivision.isChecked()){
        req.platformAmount = params.get("platformAmount");
        req.subOrders = params.get("subOrders");
        // }
        return req.toString();
    }
    static public String signWithMd5(String originStr, String md5Key, String charset) {
        String text = originStr + md5Key;
        Log.d("zhangxiulu", "signStr:" + text);
        return UnifyMd5.md5Hex(getContentBytes(text, charset)).toUpperCase();
    }

    private static byte[] getContentBytes(String content, String charset) {
        if (charset == null || "".equals(charset)) {
            return content.getBytes();
        }
        try {
            return content.getBytes(charset);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("MD5签名过程中出现错误,指定的编码集不对,您目前指定的编码集是:"
                    + charset);
        }
    }

    static public String buildSignString(Map<String, String> params) {
        List<String> keys = new ArrayList<String>(params.size());

        for (String key : params.keySet()) {
            if ("sign".equals(key) || "sign_type".equals(key))
                continue;
            if (params.get(key) == null || params.get(key).equals(""))
                continue;
            keys.add(key);
        }

        Collections.sort(keys);

        StringBuilder buf = new StringBuilder();

        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            String value = params.get(key);

            if (i == keys.size() - 1) {// 拼接时，不包括最后一个&字符
                buf.append(key + "=" + value);
            } else {
                buf.append(key + "=" + value + "&");
            }
        }

        return buf.toString();

    }

    public static byte[] httpPost(String var0, String var1) {
        if (var0 != null && var0.length() != 0) {
            HttpClient var2 = v();
            HttpPost var4 = new HttpPost(var0);

            try {
                var4.setEntity(new StringEntity(var1, "utf-8"));
                var4.setHeader("Content-Type", "text/xml;charset=UTF-8");
                HttpResponse var5;
                if ((var5 = var2.execute(var4)).getStatusLine().getStatusCode() != 200) {
                    Log.e("SDK_Sample.Util", "httpGet fail, status code = " + var5.getStatusLine().getStatusCode());
                    return null;
                } else {
                    return EntityUtils.toByteArray(var5.getEntity());
                }
            } catch (Exception var3) {
                Log.e("SDK_Sample.Util", "httpPost exception, e = " + var3.getMessage());
                var3.printStackTrace();
                return null;
            }
        } else {
            Log.e("SDK_Sample.Util", "httpPost, url is null");
            return null;
        }
    }

    private static HttpClient v() {
        try {
            KeyStore var0;
            (var0 = KeyStore.getInstance(KeyStore.getDefaultType())).load((InputStream) null, (char[]) null);
            SocketFactory var4;
            (var4 = new SocketFactory(var0)).setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            BasicHttpParams var1;
            HttpProtocolParams.setVersion(var1 = new BasicHttpParams(), HttpVersion.HTTP_1_1);
            HttpProtocolParams.setContentCharset(var1, "UTF-8");
            SchemeRegistry var2;
            (var2 = new SchemeRegistry()).register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
            var2.register(new Scheme("https", var4, 443));
            ThreadSafeClientConnManager var5 = new ThreadSafeClientConnManager(var1, var2);
            return new DefaultHttpClient(var5, var1);
        } catch (Exception var3) {
            return new DefaultHttpClient();
        }
    }


}
