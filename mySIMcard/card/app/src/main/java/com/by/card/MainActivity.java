package com.by.card;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.thinsim.model.Card;
import com.thinsim.model.Card.SCSupported;
import com.thinsim.model.Card.DuoSimReady;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.thinsim.model.Card;
import com.thinsim.model.Card.SCSupported;
import com.thinsim.model.Card.DuoSimReady;

public class MainActivity extends AppCompatActivity {


    private TextView show;
    private Card mCard = new Card();
    private ProgressDialog pg = null;
    EditText input1;
    EditText input2;
    TextView result;
    public static long startMili;
    public static long endMili;
    public static String pubRes;

    private Handler mHandler = new Handler();
    private Runnable mRunnable = new Runnable() {
        public void run() {
            Looper.prepare();
            mCard.CheckSimReady(MainActivity.this, new DuoSimReady() {

                @Override
                public void isDuoSimReady(int ready) {
                    if(ready == Card.SIM_READY) {
                        result.setText("SIM is Ready");
                    } else if(ready == Card.SIM_NOT_READY) {
                        result.setText("SIM is not Ready");
                        return;
                    } else {
                        result.setText("SIM is Ready or not");
                    }

                    showWaiting("Processing...");
                    mCard.OpenSEService(MainActivity.this, new SCSupported() {

                        @Override
                        public void isSupported(boolean success) {
                            disWaiting();
                            if(success) {
                                showToast("OTI Channel: " + mCard.getOtiType());
                            }else{
                                showToast("Not Support OTI");
                            }
                        }
                    });
                }
            });
            Looper.loop();
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();

        mCard.setPrintLog(true);
//		mCard.setHighSpeedChannel1Used(false);
//		mCard.setHighSpeedChannel2Used(false);
//		mCard.setLowSpeedChannel1Used(false);
//		mCard.setLowSpeedChannel2Used(false);

//		mHandler.post(mRunnable);
        new Thread(mRunnable).start();

//		mCard.OpenSEService(this, new SCSupported() {
//
//			@Override
//			public void isSupported(boolean success) {
//				if(success) {
//					showToast("支持OTI！");
//				}else{
//					showToast("不支持OTI！");
//				}
//			}
//		});
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mCard.CloseSEService();
    }

    private void showWaiting(String msg) {
        // if(pg == null) {
        pg = new ProgressDialog(this);
        // }
        pg.setIndeterminate(true);
        pg.setCancelable(false);
        pg.setCanceledOnTouchOutside(false);
        pg.setMessage(msg);
        pg.show();
    }

    private void disWaiting() {
        if (pg != null && pg.isShowing()) {
            pg.dismiss();
        }
    }

    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBackPressed() {

        mCard.CloseSEService();

        finish();
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
        //android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);

        //moveTaskToBack(true);
    }

    private void initView() {
        show = (TextView) findViewById(R.id.show);
        show.setText("====================================\n" +
                "版本：20200118-1\n" +
                "====================================");
        result = (TextView) findViewById(R.id.result);

        Button test = (Button) findViewById(R.id.enc1024);
        test.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                startMili=System.currentTimeMillis();
//				mCard.SendAPDU("B017000082002A0102030405060708090001020304050607080900010203040506070809000102030405060708090001020304050607080900010203040506070809000102030405060708090001020304050607080900010203040506070809000102030405060708090001020304050607080900010203040506070809000102030405060708", new StringArrayResponse() {
//
//					@Override
//					public void stringArrayResponse(String[] res) {
//						if(res[0].equals(Card.XKR_OK)){
//							showToast("1024加密成功！");
//						}else{
//							showToast("1024加密失败！");
//						}
//
//						endMili=System.currentTimeMillis();
//						result.setText("Time: " + (endMili-startMili) + "ms\n" + res[1]);
//					}
//				});

                String res = mCard.SendAPDU("B017000082002A0102030405060708090001020304050607080900010203040506070809000102030405060708090001020304050607080900010203040506070809000102030405060708090001020304050607080900010203040506070809000102030405060708090001020304050607080900010203040506070809000102030405060708");
                if(res != null){
                    endMili=System.currentTimeMillis();
                    result.setText("Time: " + (endMili-startMili) + "ms\n" + res);
                    pubRes = res.substring(0, res.length()-4);
                }else{
                    result.setText("1024加密失败！");
                }
            }
        });

        Button read = (Button) findViewById(R.id.dec1024);
        read.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if(pubRes.length() != 256){
                    result.setText("请先执行1024加密！");
                    return;
                }

                startMili=System.currentTimeMillis();
//				mCard.SendAPDU("B017000082002B2A5D35F136BF82560FC91506C410FD5327688E8DD70487984F5EE9FB6E3D48C00FC165950972353C865B61D31FB81C835AE4386D08FA6094F19C26338DACF2254D9F2226003542CACBCB4BB934D2109E44BFE270B873FE89863988E8279224C148DCAAAE627B5CE39E78F88949B762E8596800D90F49C8D59C816E0154AD6AC1", new StringArrayResponse() {
//
//					@Override
//					public void stringArrayResponse(String[] res) {
//						if(res[0].equals(Card.XKR_OK)){
//							showToast("1024解密成功！");
//						}else{
//							showToast("1024解密失败！");
//						}
//
//						endMili=System.currentTimeMillis();
//						result.setText("Time: " + (endMili-startMili) + "ms\n" + res[1]);
//					}
//				});

//				String res = mCard.SendAPDU("B017000082002B2A5D35F136BF82560FC91506C410FD5327688E8DD70487984F5EE9FB6E3D48C00FC165950972353C865B61D31FB81C835AE4386D08FA6094F19C26338DACF2254D9F2226003542CACBCB4BB934D2109E44BFE270B873FE89863988E8279224C148DCAAAE627B5CE39E78F88949B762E8596800D90F49C8D59C816E0154AD6AC1");
                String res = mCard.SendAPDU("B017000082002B" + pubRes);
                if(res != null){
                    endMili=System.currentTimeMillis();
                    result.setText("Time: " + (endMili-startMili) + "ms\n" + res);
                }else{
                    result.setText("1024解密失败！");
                }
            }
        });

        Button enc2048 = (Button) findViewById(R.id.enc2048);
        enc2048.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                startMili=System.currentTimeMillis();
                String res = mCard.SendAPDU("B017000182002D1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456");
                res = mCard.SendAPDU("B0170003807890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012");
                if(res != null){
                    endMili=System.currentTimeMillis();
                    result.setText("Time: " + (endMili-startMili) + "ms\n" + res);
                    pubRes = res.substring(0, res.length()-4);
                }else{
                    result.setText("2048加密失败！");
                }
            }
        });

        Button dec2048 = (Button) findViewById(R.id.dec2048);
        dec2048.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if(pubRes.length() != 512){
                    result.setText("请先执行2048加密！");
                    return;
                }

                startMili=System.currentTimeMillis();
                String res = mCard.SendAPDU("B017000182002C" + pubRes.substring(0, 256));
                res = mCard.SendAPDU("B017000380" + pubRes.substring(256));
                if(res != null){
                    endMili=System.currentTimeMillis();
                    result.setText("Time: " + (endMili-startMili) + "ms\n" + res);
                }else{
                    result.setText("2048解密失败！");
                }
            }
        });


        /*SM2 加密*/
        Button sm2Enc = (Button) findViewById(R.id.SM2pub);
        sm2Enc.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                startMili=System.currentTimeMillis();
//				mCard.SendAPDU("B02A000098002E123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890", new StringArrayResponse() {
//
//					@Override
//					public void stringArrayResponse(String[] res) {
//						if(res[0].equals(Card.XKR_OK)){
//							showToast("加密成功！");
//						}else{
//							showToast("加密失败！");
//						}
//
//						endMili=System.currentTimeMillis();
//						result.setText("Time: " + (endMili-startMili) + "ms\n" + res[1]);
//					}
//				});

//				String res = mCard.SendAPDU("B02A000098002E123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890");
                String res = mCard.SendAPDU("B02A000096002E12345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456");
                if(res != null){
                    endMili=System.currentTimeMillis();
                    result.setText("Time: " + (endMili-startMili) + "ms\n" + res);
                    pubRes = res.substring(0, res.length()-4);
                }else{
                    result.setText("加密失败！");
                }
            }
        });

        /*SM2 解密*/
        Button sm2Dec = (Button) findViewById(R.id.SM2pri);
        sm2Dec.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if(pubRes == null){
                    result.setText("请先执行SM2加密！");
                    return;
                }

                startMili=System.currentTimeMillis();
//				mCard.SendAPDU("B02A0000F7002F04161C0BA92EBD89DFD91E714698B86DA9459359D42CD1F609A28DE5346018C2A5863EDAB56490F64E64F6E302CF5DA021A63590F07FBEE6C6AC27613986BED661690FE6147E949DCD95B4FB34813602B101F186D4B073F50D1977216F8EEA14CC5E6A7557DD09F61E3629D5A40B307021F17E9A4C509F7452E04B879CC0E965F4D0D2148E32CC8D83B6BEB264577139191EB2895D4181CCA97032801A86EF4A4B96B25B33497BC9D9C4B3CF9057ADCB183CFA8E5D2A2EFF754B86A28045F369BDCBC7BE655B73D04CEAD85F2D754CD4B38BFA38E3B4F641B4B58DC6B332CEAD10492F29EB0CB426F4C89DF40088AB8FB914E1CAF1", new StringArrayResponse() {
//
//					@Override
//					public void stringArrayResponse(String[] res) {
//						if(res[0].equals(Card.XKR_OK)){
//							showToast("解密成功！");
//						}else{
//							showToast("解密失败！");
//						}
//
//						endMili=System.currentTimeMillis();
//						result.setText("Time: " + (endMili-startMili) + "ms\n" + res[1]);
//					}
//				});

//				String res = mCard.SendAPDU("B02A0000F7002F04161C0BA92EBD89DFD91E714698B86DA9459359D42CD1F609A28DE5346018C2A5863EDAB56490F64E64F6E302CF5DA021A63590F07FBEE6C6AC27613986BED661690FE6147E949DCD95B4FB34813602B101F186D4B073F50D1977216F8EEA14CC5E6A7557DD09F61E3629D5A40B307021F17E9A4C509F7452E04B879CC0E965F4D0D2148E32CC8D83B6BEB264577139191EB2895D4181CCA97032801A86EF4A4B96B25B33497BC9D9C4B3CF9057ADCB183CFA8E5D2A2EFF754B86A28045F369BDCBC7BE655B73D04CEAD85F2D754CD4B38BFA38E3B4F641B4B58DC6B332CEAD10492F29EB0CB426F4C89DF40088AB8FB914E1CAF1");
                String res = mCard.SendAPDU("B02A0000F7002F" + pubRes);
                if(res != null){
                    endMili=System.currentTimeMillis();
                    result.setText("Time: " + (endMili-startMili) + "ms\n" + res);
                }else{
                    result.setText("解密失败！");
                }
            }
        });

        /*SM2 签名*/
        Button SM2sign = (Button) findViewById(R.id.SM2sign);
        SM2sign.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                startMili=System.currentTimeMillis();
//				mCard.SendAPDU("B02C000022002F0B9BF4234B39601DF0A4AB86EA7AA253781ED3F5BCF46A18BD059807E7FE82EE", new StringArrayResponse() {
//
//					@Override
//					public void stringArrayResponse(String[] res) {
//						if(res[0].equals(Card.XKR_OK)){
//							showToast("签名成功！");
//						}else{
//							showToast("签名失败！");
//						}
//
//						endMili=System.currentTimeMillis();
//						result.setText("Time: " + (endMili-startMili) + "ms\n" + res[1]);
//					}
//				});

                String res = mCard.SendAPDU("B02C000022002F0B9BF4234B39601DF0A4AB86EA7AA253781ED3F5BCF46A18BD059807E7FE82EE");
                if(res != null){
                    endMili=System.currentTimeMillis();
                    result.setText("Time: " + (endMili-startMili) + "ms\n" + res);
                    pubRes = res.substring(0, res.length()-4);
                }else{
                    result.setText("签名失败！");
                }
            }
        });


        Button SM2verify = (Button) findViewById(R.id.SM2verify);
        SM2verify.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                startMili=System.currentTimeMillis();
//				mCard.SendAPDU("B021000062002E7DCE3B05D35D84F8A054F7326BB335F1D75BDB23DAAF0C5CB30EFAB24C063D8CA20102957AE456A7B7CAC515F4679134EE247CE8A034F131275564CAC5D3813D0B9BF4234B39601DF0A4AB86EA7AA253781ED3F5BCF46A18BD059807E7FE82EE", new StringArrayResponse() {
//
//					@Override
//					public void stringArrayResponse(String[] res) {
//						if(res[0].equals(Card.XKR_OK)){
//							showToast("验签成功！");
//						}else{
//							showToast("验签失败！");
//						}
//
//						endMili=System.currentTimeMillis();
//						result.setText("Time: " + (endMili-startMili));
//					}
//				});

//				String res = mCard.SendAPDU("B021000062002E7DCE3B05D35D84F8A054F7326BB335F1D75BDB23DAAF0C5CB30EFAB24C063D8CA20102957AE456A7B7CAC515F4679134EE247CE8A034F131275564CAC5D3813D0B9BF4234B39601DF0A4AB86EA7AA253781ED3F5BCF46A18BD059807E7FE82EE");
                String res = mCard.SendAPDU("B021000062002E" + pubRes + "0B9BF4234B39601DF0A4AB86EA7AA253781ED3F5BCF46A18BD059807E7FE82EE");
                if(res != null){
                    endMili=System.currentTimeMillis();
//					result.setText("Time: " + (endMili-startMili) + "ms\n" + res);
                    result.setText("Time: " + (endMili-startMili) + "ms\n" + "验签成功！");
                }else{
                    result.setText("验签失败！");
                }
            }
        });

        input1 = (EditText) findViewById(R.id.input1);
        input2 = (EditText) findViewById(R.id.input2);

        Button send = (Button) findViewById(R.id.send);
        send.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                result.setText("");
                String apdu = input1.getText().toString();
                startMili=System.currentTimeMillis();
//				mCard.SendAPDU(apdu, new StringArrayResponse() {
//					@Override
//					public void stringArrayResponse(String[] res) {
//						if(res[0].equals(Card.XKR_OK)){
//							showToast("发送成功！");
//						}else{
//							showToast("发送失败！");
//						}
//
//						endMili=System.currentTimeMillis();
//						result.setText("Time: " + (endMili-startMili) + "ms\n" + res[1]);
//					}
//				});

                String res = mCard.SendAPDU(apdu);
                if(res != null){
                    endMili=System.currentTimeMillis();
                    result.setText("Time: " + (endMili-startMili) + "ms\n" + res);
                }else{
                    result.setText("发送失败！");
                }
            }
        });

        Button verify = (Button) findViewById(R.id.verify);
        verify.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                result.setText("");
                byte[] pin = input1.getText().toString().getBytes();
                startMili=System.currentTimeMillis();
//				mCard.VerifyPIN(1, pin, new StringResponse(){
//					@Override
//					public void stringResponse(String string) {
//						endMili=System.currentTimeMillis();
//
//						if(string.equals(Card.XKR_OK)){
//							if(mCard.getOtiType() != null){
//								result.setText("Channel: " + mCard.getOtiType() + "\n" + "Time: " + (endMili-startMili) + "ms\n" + "校验成功！");
//							}else{
//								result.setText("Time: " + (endMili-startMili) + "ms\n" + "校验成功！");
//							}
//						}else if(Integer.parseInt(string) > 0){
//							if(mCard.getOtiType() != null){
//								result.setText("Channel: " + mCard.getOtiType() + "\n" + "Time: " + (endMili-startMili) + "ms\n"
//												+ "校验失败！剩余次数：" + string);
//							}else{
//								result.setText("Time: " + (endMili-startMili) + "ms\n" + "校验失败！剩余次数：" + string);
//							}
//						}else if(string.equals(Card.XKR_KEY_LOCKED)){
//							if(mCard.getOtiType() != null){
//								result.setText("Channel: " + mCard.getOtiType() + "\n" + "Time: " + (endMili-startMili) + "ms\n"
//												+ "校验失败！已锁卡，请输入PUK码解锁！");
//							}else{
//								result.setText("Time: " + (endMili-startMili) + "ms\n" + "校验失败！已锁卡，请输入PUK码解锁！");
//							}
//						}else{
//							if(mCard.getOtiType() != null){
//								result.setText("Channel: " + mCard.getOtiType() + "\n" + "错误码：" + string);
//							}else{
//								result.setText("错误码：" + string);
//							}
//						}
//					}
//				});

                int res = mCard.VerifyPIN(1, pin);
                endMili=System.currentTimeMillis();

                if(res == Card.XKR_OK){
                    if(mCard.getOtiType() != null){
                        result.setText("Channel: " + mCard.getOtiType() + "\n" + "Time: " + (endMili-startMili) + "ms\n" + "校验成功！");
                    }else{
                        result.setText("Time: " + (endMili-startMili) + "ms\n" + "校验成功！");
                    }
                }else if(res > 0){
                    if(mCard.getOtiType() != null){
                        result.setText("Channel: " + mCard.getOtiType() + "\n" + "Time: " + (endMili-startMili) + "ms\n"
                                + "校验失败！剩余次数：" + res);
                    }else{
                        result.setText("Time: " + (endMili-startMili) + "ms\n" + "校验失败！剩余次数：" + res);
                    }
                }else if(res == Card.XKR_KEY_LOCKED){
                    if(mCard.getOtiType() != null){
                        result.setText("Channel: " + mCard.getOtiType() + "\n" + "Time: " + (endMili-startMili) + "ms\n"
                                + "校验失败！已锁卡，请输入PUK码解锁！");
                    }else{
                        result.setText("Time: " + (endMili-startMili) + "ms\n" + "校验失败！已锁卡，请输入PUK码解锁！");
                    }
                }else{
                    if(mCard.getOtiType() != null){
                        result.setText("Channel: " + mCard.getOtiType() + "\n" + "错误码：" + res);
                    }else{
                        result.setText("错误码：" + res);
                    }
                }
            }
        });

        Button change = (Button) findViewById(R.id.change);
        change.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                result.setText("");
                byte[] oldPIN = input1.getText().toString().getBytes();
                byte[] newPIN = input2.getText().toString().getBytes();
                startMili=System.currentTimeMillis();
//				mCard.ChangePIN(1, oldPIN, newPIN, new StringResponse() {
//
//					@Override
//					public void stringResponse(String string) {
//						endMili=System.currentTimeMillis();
//						if(string.equals(Card.XKR_OK)){
//							result.setText("Time: " + (endMili-startMili) + "ms\n" + "修改成功！");
//						}else if(Integer.parseInt(string) > 0){
//							result.setText("Time: " + (endMili-startMili) + "ms\n" + "修改失败！剩余次数：" + string);
//						}else if(string.equals(Card.XKR_KEY_LOCKED)){
//							result.setText("Time: " + (endMili-startMili) + "ms\n" + "修改失败！已锁卡，请输入PUK码解锁！");
//						}else{
//							result.setText("错误码：" + string);
//						}
//					}
//				});

                int res = mCard.ChangePIN(1, oldPIN, newPIN);
                endMili=System.currentTimeMillis();
                if(res == Card.XKR_OK){
                    result.setText("Time: " + (endMili-startMili) + "ms\n" + "修改成功！");
                }else if(res > 0){
                    result.setText("Time: " + (endMili-startMili) + "ms\n" + "修改失败！剩余次数：" + res);
                }else if(res == Card.XKR_KEY_LOCKED){
                    result.setText("Time: " + (endMili-startMili) + "ms\n" + "修改失败！已锁卡，请输入PUK码解锁！");
                }else{
                    result.setText("错误码：" + res);
                }
            }
        });

        Button unlock = (Button) findViewById(R.id.unlock);
        unlock.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                result.setText("");
                byte[] pin = input1.getText().toString().getBytes();
                byte[] puk = input2.getText().toString().getBytes();
                startMili=System.currentTimeMillis();
//				mCard.UnlockPIN(1, puk, pin, new StringResponse(){
//
//					@Override
//					public void stringResponse(String string) {
//						endMili=System.currentTimeMillis();
//
//						if(string.equals(Card.XKR_OK)){
//							result.setText("Time: " + (endMili-startMili) + "ms\n" + "解锁成功！");
//						}else if(Integer.parseInt(string) > 0){
//							result.setText("Time: " + (endMili-startMili) + "ms\n" + "解锁失败！剩余次数：" + string);
//						}else if(string.equals(Card.XKR_KEY_LOCKED)){
//							result.setText("Time: " + (endMili-startMili) + "ms\n" + "解锁失败！已锁卡！");
//						}else{
//							result.setText("错误码：" + string);
//						}
//					}
//				});

                int res = mCard.UnlockPIN(1, puk, pin);
                endMili=System.currentTimeMillis();
                if(res == Card.XKR_OK){
                    result.setText("Time: " + (endMili-startMili) + "ms\n" + "解锁成功！");
                }else if(res > 0){
                    result.setText("Time: " + (endMili-startMili) + "ms\n" + "解锁失败！剩余次数：" + res);
                }else if(res == Card.XKR_KEY_LOCKED){
                    result.setText("Time: " + (endMili-startMili) + "ms\n" + "解锁失败！已锁卡！");
                }else{
                    result.setText("错误码：" + res);
                }
            }
        });

//		Button runRSA2048 = (Button) findViewById(R.id.runRSA2048);
//		runRSA2048.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				startMili=System.currentTimeMillis();
//				for(int i = 0; i < 100; i++){
//					String res = mCard.SendAPDU("B017010016002D0011223344556677889900112233445566778899");
//					endMili=System.currentTimeMillis();
//					if(res != null && res.length()/2 == 258){
//						result.setText("Time: " + (endMili-startMili) + "ms\nCount = "
//											+ Integer.toString(i) + "\n" + res + "\n2048加密成功！");
//					}else if(res == null){
//						result.setText("Time: " + (endMili-startMili) + "ms\nCount = "
//											+ Integer.toString(i) + "res = null\n2048加密失败！");
//						break;
//					}else{
//						result.setText("Time: " + (endMili-startMili) + "ms\nCount = "
//											+ Integer.toString(i) + "\n" + "res =" + res + "\n2048加密失败！");
//						break;
//					}
//				}
//			}
//		});

//		Button genRSAKey2048 = (Button) findViewById(R.id.genRSAKey2048);
//		genRSAKey2048.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				startMili=System.currentTimeMillis();
//				String res = mCard.SendAPDU("B026020004002C002D");
//				endMili=System.currentTimeMillis();
//				if(res != null){
//					result.setText("Time: " + (endMili-startMili) + "ms\nres = " + res);
//				}else{
//					result.setText("Time: " + (endMili-startMili) + "ms\nres = null\n");
//				}
//
//			}
//		});
//
//		Button readFile = (Button) findViewById(R.id.readFile);
//		readFile.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				startMili=System.currentTimeMillis();
//				String res = mCard.SendAPDU("B0A4000C02002F");
//				if(res != null && res.equals("9000")) {
//					res = mCard.SendAPDU("B0B0000002");
//				}
//				endMili=System.currentTimeMillis();
//				if(res != null){
//					result.setText("Time: " + (endMili-startMili) + "ms\nres = " + res);
//				}else{
//					result.setText("Time: " + (endMili-startMili) + "ms\nres = null\n");
//				}
//
//			}
//		});

        Button update = (Button) findViewById(R.id.update);
        update.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                String res = mCard.SendAPDU("B010000000");
                if(res == null || !res.substring(res.length()-4).equals("9000")) {
                    return;
                }

                int ver = Integer.valueOf(res.substring(78, 80));
                if(ver <= 33) {
                    result.setText("Do nothing");
                    return;
                } else if(ver == 35) {
                    result.setText("Turn on compability mode in STK");
                    return;
                } else if(ver >= 36) {
                    result.setText("Supported already");
                    return;
                }

                String[] apdu = {"B00400000243AB", "B0E00000080100F0F0F0F043AB", "B0A4000C0243AB",
//						"B0D60000C2C1FF40BEE224E116C1144216CEC0D2C5F0C29D23C4F832242E639D5846A8E30ADB080000000000000001E224E116C11443BD026B9D53D7F500B2BCBDBB345BF1CDEF7FC0E30ADB080000000000000001E224E116C11461ED377E85D386A8DFEE6B864BD85B0BFAA5AF81E30ADB080000000000000001E224E116C1140287CE6CA15C6DF0CAE3598E2336658E364550E0E30ADB080000000000000001E224E116C1149371989A855558EBEC063ABA2248DE9A651C7C25E30ADB080000000000000001"};
//						"B0D600002A29FF4026E224E116C1149371989A855558EBEC063ABA2248DE9A651C7C25E30ADB080000000000000001"};
                        "B0D600007675FF4072E224E116C1144216CEC0D2C5F0C29D23C4F832242E639D5846A8E30ADB080000000000000001E224E116C11443BD026B9D53D7F500B2BCBDBB345BF1CDEF7FC0E30ADB080000000000000001E224E116C1149371989A855558EBEC063ABA2248DE9A651C7C25E30ADB080000000000000001"};
                for(int i = 0; i < apdu.length; i++) {
                    res = mCard.SendAPDU(apdu[i]);
                    if(res == null || !res.equals("9000")) {
                        result.setText("Update failed");
                        break;
                    } else {
                        result.setText("Update success");
                    }
                }
            }
        });

        Button closeHighSpeed = (Button) findViewById(R.id.closeHighSpeed);
        closeHighSpeed.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
//				mCard.CloseHighSpeedChannel1();
//				mCard.CloseHighSpeedChannel2();
                mCard.CloseSEService();
            }
        });

    }

}