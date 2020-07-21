package oms.masm.permissionx

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import oms.masm.library.permissionx.PermissionX

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button.setOnClickListener {
            PermissionX.init(this).request(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CALL_PHONE) { allGranted, grantedList, deniedList ->
                    if (allGranted) {
                        Toast.makeText(this@MainActivity, "已同意", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@MainActivity, "已拒绝", Toast.LENGTH_SHORT).show()
                    }
                }

        }
    }
}
