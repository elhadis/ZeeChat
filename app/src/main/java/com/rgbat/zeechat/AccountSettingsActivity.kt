package com.rgbat.zeechat

import Model.User
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
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.activity_account_settings.*
import kotlinx.android.synthetic.main.fragment_profile.view.*

class AccountSettingsActivity : AppCompatActivity() {
    private lateinit var firebaseUser: FirebaseUser
    private  var checker = ""
    private  var mUrl = ""
    private var imageUri :Uri? = null
    private   var stroageProfilePicRef :StorageReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_settings)
        firebaseUser = FirebaseAuth.getInstance().currentUser!!

        stroageProfilePicRef = FirebaseStorage.getInstance().reference.child("Profile Pictures")

        logout_btn.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this, SigninActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }

        change_image_text_btn.setOnClickListener {

            checker = "clicked"

            CropImage.activity()
                .setAspectRatio(1,1)
                .start(this@AccountSettingsActivity)
        }



        save_info_profile_btn.setOnClickListener {

            if (checker == "clicked"){


                updateImageAndUpdateInfo()
            }

            else{

                 updateUserInfoOnly()
            }

        }
        userInfo()


    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode ==
                Activity.RESULT_OK && data != null){
              val  result = CropImage.getActivityResult(data)
            imageUri = result.uri
            profile_image_view_profile_frag.setImageURI(imageUri)

        }



    }



    private fun updateUserInfoOnly() {

        when {
            fullname_profil_frag.toString() == "" -> {
                Toast.makeText( this,"please write full name first",Toast.LENGTH_LONG).show()
            }
            username_profile_frag.toString() == "" -> {
                Toast.makeText( this,"please write User name first",Toast.LENGTH_LONG).show()
            }
            bio_profile_frag.toString() == "" -> {
                Toast.makeText( this,"please write Bio first",Toast.LENGTH_LONG).show()
            }
            else -> {

                val userRef = FirebaseDatabase.getInstance().reference.child("Users")

                val userMap = HashMap<String,Any>()

                userMap["fullname"] = fullname_profil_frag.text.toString().toLowerCase()
                userMap["username"] = username_profile_frag.text.toString().toLowerCase()

                userMap["biography"] = bio_profile_frag.text.toString().toLowerCase()
                userRef.child(firebaseUser.uid).updateChildren(userMap)

                Toast.makeText( this,"Account Information hase been Successfully",Toast.LENGTH_LONG).show()

                val intent = Intent(this,MainActivity::class.java)

                startActivity(intent)
                finish()
            }
        }
        }



    private fun userInfo() {

        val userRef = FirebaseDatabase.getInstance().getReference().child("Users")
            .child(firebaseUser.uid)
        userRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

//                if (context != null){
//                    return
//                }
                if (snapshot.exists()) {
                    val user = snapshot.getValue<User>(User::class.java)

                    Picasso.get().load(user!!.getImage()).placeholder(R.drawable.profile)
                        .into(profile_image_view_profile_frag)
                    username_profile_frag.setText(user!!.getUsername())
                    fullname_profil_frag.setText(user!!.getFullname())
                    bio_profile_frag.setText(user!!.getBiography())
                }

            }

            override fun onCancelled(error: DatabaseError) {

            }


        })
    }

    private fun updateImageAndUpdateInfo() {


        val progresDialog = ProgressDialog(this)
        progresDialog.setTitle("Account Settings")
        progresDialog.setMessage("Please Wait")
        progresDialog.show()

        when {

           imageUri == null ->
                Toast.makeText(this, "please Select image", Toast.LENGTH_LONG).show()


            fullname_profil_frag.toString() == "" ->
                Toast.makeText(this, "please write full name first", Toast.LENGTH_LONG).show()

            username_profile_frag.toString() == "" ->
                Toast.makeText(this, "please write User name first", Toast.LENGTH_LONG).show()

            bio_profile_frag.toString() == "" ->
                Toast.makeText(this, "please write Bio first", Toast.LENGTH_LONG).show()


            else ->{

                val fileRef = stroageProfilePicRef!!.child(firebaseUser!!.uid + ".Jpg")


                var uploadTask:StorageTask<*>
                uploadTask = fileRef.putFile(imageUri!!)

                uploadTask.continueWithTask(Continuation <UploadTask.TaskSnapshot,Task<Uri>>{ task ->
                    if (!task.isSuccessful){
                        task.exception?.let {
                            throw it
                            progresDialog.dismiss()
                        }
                    }
                    return@Continuation fileRef.downloadUrl
                }).addOnCompleteListener(OnCompleteListener <Uri>{ task ->

                    if (task.isSuccessful){

                        val downLoadUri =task.result
                        mUrl = downLoadUri.toString()
                        val ref = FirebaseDatabase.getInstance().reference.child("Users")

                        val userMap = HashMap<String,Any>()

                        userMap["fullname"] = fullname_profil_frag.text.toString().toLowerCase()
                        userMap["username"] = username_profile_frag.text.toString().toLowerCase()

                        userMap["biography"] = bio_profile_frag.text.toString().toLowerCase()
                        userMap["image"] = mUrl

                        ref.child(firebaseUser.uid).updateChildren(userMap)

                        Toast.makeText( this,"Account Information hase been Successfully",Toast.LENGTH_LONG).show()

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
