package com.rgbat.zeechat

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_sign_up.*
import kotlinx.android.synthetic.main.activity_signin.*

class SigninActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signin)

        signup_link_btn .setOnClickListener {
            startActivity(Intent(this,SignUpActivity::class.java))
        }
        login_btn.setOnClickListener {

            loginUser()
        }
    }

    private fun loginUser() {

        val email = email_login.text.toString()
        val password = password_login.text.toString()

        when{
            TextUtils.isEmpty(email)-> Toast.makeText( this,"Email is require", Toast.LENGTH_LONG).show()
            TextUtils.isEmpty(password)-> Toast.makeText( this,"password is require", Toast.LENGTH_LONG).show()

            else ->{


                val progressDialog = ProgressDialog(this)
                progressDialog.setTitle("Login")
                progressDialog.setMessage("Please Wait....")
                progressDialog.setCanceledOnTouchOutside(false)
                progressDialog.show()

                val mAuth: FirebaseAuth =FirebaseAuth.getInstance()
                mAuth.signInWithEmailAndPassword(email,password)
                    .addOnCompleteListener { task ->

                        if (task.isSuccessful){

                            progressDialog.dismiss()

                            val intent = Intent(this,MainActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)

                        }
                        else{
                            val message = task.exception!!.toString()
                            Toast.makeText( this,"Error $message",Toast.LENGTH_LONG).show()
                            mAuth.signOut()
                            progressDialog.dismiss()
                        }
                    }

            }
        }

    }


    override fun onStart() {
        super.onStart()
        if (FirebaseAuth.getInstance().currentUser != null){

            val intent = Intent(this,MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)


        }
    }
}
