package com.rgbat.zeechat

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.activity_account_settings.*
import kotlinx.android.synthetic.main.activity_add_p_ost.*

class AddPOstActivity : AppCompatActivity() {

    private  var mUrl = ""
    private var imageUri : Uri? = null
    private   var stroagePostPicRef : StorageReference? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_p_ost)

        stroagePostPicRef = FirebaseStorage.getInstance().reference.child("Posts Pictures")

        save_new_post_btn.setOnClickListener {

            UploadImage()

        }
        CropImage.activity()
            .setAspectRatio(10,10)
            .start(this@AddPOstActivity)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode ==
            Activity.RESULT_OK && data != null){
            val  result = CropImage.getActivityResult(data)
            imageUri = result.uri
           image_post.setImageURI(imageUri)

        }

    }

    private fun UploadImage() {


        when {

            description_post.toString() == "" ->
                Toast.makeText(this, "please write YourPost Description", Toast.LENGTH_LONG).show()

            image_post.toString() == "" ->
                Toast.makeText(this, "please select Image", Toast.LENGTH_LONG).show()

            else -> {


                val progresDialog = ProgressDialog(this)
                progresDialog.setTitle("Adding New Post")
                progresDialog.setMessage("Please Wait")
                progresDialog.show()


                val fileRef = stroagePostPicRef!!.child(System.currentTimeMillis().toString() + ".Jpg")

                var uploadTask: StorageTask<*>
                uploadTask = fileRef.putFile(imageUri!!)

                uploadTask.continueWithTask(Continuation <UploadTask.TaskSnapshot, Task<Uri>>{ task ->
                    if (!task.isSuccessful){
                        task.exception?.let {
                            throw it
                            progresDialog.dismiss()
                        }
                    }
                    return@Continuation fileRef.downloadUrl
                })
                    .addOnCompleteListener(OnCompleteListener <Uri>{ task ->

                        if (task.isSuccessful){

                            val downLoadUri =task.result
                            mUrl = downLoadUri.toString()

                            val ref = FirebaseDatabase.getInstance().reference.child("Posts")
                            val postId= ref.push().key

                            val postMap = HashMap<String,Any>()

                            postMap["postid"] = postId!!
                            postMap["description"] = description_post.text.toString()

                            postMap["publisher"] = FirebaseAuth.getInstance().currentUser!!.uid
                            postMap["postimage"] = mUrl

                            ref.child(postId).updateChildren(postMap)

                            Toast.makeText( this,"Post Uploaded Successfully",Toast.LENGTH_LONG).show()

                            val intent = Intent(this,MainActivity::class.java)

                            startActivity(intent)
                            finish()
                            progresDialog.dismiss()

                        }
                        else{
                            progresDialog.dismiss()
                        }
                    })
            }
        }

    }

}