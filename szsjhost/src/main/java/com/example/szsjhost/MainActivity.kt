package com.example.szsjhost

import android.content.ComponentName
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import android.content.Intent.ACTION_VIEW
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.net.Uri

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btCreateConf.setOnClickListener {
            createConf()
        }

        btJoinConf.setOnClickListener {
            joinConf()
        }
    }

    private fun joinConf() {
        var smcConfId = etSmcConfId.text
        showToast(smcConfId.toString())

//        startVideoActivity(getJoinIntent())
    }

    private fun createConf() {
        var confName = etConfName.text
        var sites = etSites.text
        var siteUri = etName.text
        var sitePwd = etPwd.text

        if (confName.isEmpty() || sites.isEmpty()) {
            showToast("会议名称和参会列表都不能为空")
            return
        }

        if (siteUri.isEmpty() || sitePwd.isEmpty()) {
            showToast("用户名和密码不能为空")
            return
        }

        startVideoActivity(getCreateIntent())
    }

    private fun getBaseIntent(): Intent {

        var intent = Intent()
        var cn = ComponentName(
            "com.zxwl.frame",
            "com.hw.kotlinmvpandroidxframe.ui.activity.MainActivity"
        )

        intent.setComponent(cn)
        intent.putExtra(HuaweiContants.FILED_DISPLAY_NAME, "0000010006");
        intent.putExtra(HuaweiContants.FILED_USER_NAME, "0000010006");
        intent.putExtra(HuaweiContants.FILED_PASS_WORD, etPwd.text.toString());

        intent.putExtra(HuaweiContants.FILED_HUAWEI_SMC_URL, "113.57.147.173");
        intent.putExtra(HuaweiContants.FILED_HUAWEI_SMC_PORT, "5061");

        intent.putExtra(HuaweiContants.FILED_APP_PACKAGE_NAME, "FILED_APP_PACKAGE_NAME");
        intent.putExtra(HuaweiContants.FILED_SECRET_KEY, "FILED_SECRET_KEY");
        return intent
    }

    private fun getCreateIntent(): Intent {
        var intent = getBaseIntent()

        //类型创建会议
        intent.putExtra(HuaweiContants.FILED_TYPE, HuaweiContants.TYPE_CREATE_CONF)

        intent.putExtra(HuaweiContants.FILED_CONF_NAME, etConfName.text.toString())
        intent.putExtra(HuaweiContants.FILED_DURATION, "180")
        intent.putExtra(HuaweiContants.FILED_SITES, "0000010118,0000010119")

        return intent
    }

    var scheme = "com.zxwl.plugin"
    var host = "MainActivity"

    private fun startVideoActivity(intent: Intent) {
        startActivity(intent)
    }

    private fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
    }
}
