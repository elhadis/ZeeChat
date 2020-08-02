package Fragments

import Model.User
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.rgbat.zeechat.AccountSettingsActivity

import com.rgbat.zeechat.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_profile.view.*

/**
 * A simple [Fragment] subclass.
 */
class ProfileFragment : Fragment() {

    private lateinit var profileid: String
    private lateinit var firebaseUser: FirebaseUser

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        firebaseUser = FirebaseAuth.getInstance().currentUser!!

        val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)

        if (pref != null) {

            this.profileid = pref.getString("Profileid", "none").toString()

        }
        if (profileid == firebaseUser.uid) {

            view.edit_account_setting_btn.text = "Edit Profile"

        } else if (profileid != firebaseUser.uid) {

            checkFollowAndFollowingButtonStatuse()

        }

        view.edit_account_setting_btn.setOnClickListener {
            startActivity(Intent(context, AccountSettingsActivity::class.java))
        }



        getFollowers()
        getFollowings()
        userInfo()

        return view
    }

    private fun checkFollowAndFollowingButtonStatuse() {

        val followingRef = firebaseUser?.uid.let { it1 ->

            FirebaseDatabase.getInstance().reference.child("Follow")
                .child(it1.toString())
                .child("Following")
        }
        if (followingRef != null) {

            followingRef.addValueEventListener(object : ValueEventListener {


                override fun onDataChange(snapshot: DataSnapshot) {

                    if (snapshot.child(profileid).exists()) {

                        view?.edit_account_setting_btn?.text = "Following"

                    } else {
                        view?.edit_account_setting_btn?.text = "Follow"
                    }

                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
        }

    }

    private fun getFollowers() {


        val followersRef =   FirebaseDatabase.getInstance().reference.child("Follow")
            .child(profileid)

            .child("Followers")

        followersRef.addValueEventListener(object : ValueEventListener {




            override fun onDataChange(snapshot: DataSnapshot) {


                if (snapshot.exists()) {
                    view?.total_followers?.text = snapshot.childrenCount.toString()
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

    }

    private fun getFollowings() {


        val followersRef =  FirebaseDatabase.getInstance().reference.child("Follow")
            .child(profileid)
            .child("Following")
        followersRef.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {


                if (snapshot.exists()) {
                    view?.total_following?.text = snapshot.childrenCount.toString()
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }
    private fun userInfo(){

        val  userRef = FirebaseDatabase.getInstance().getReference().child("Users")
            .child(profileid)
        userRef.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

//                if (context != null){
//                    return
//                }
                if (snapshot.exists()){
                    val user = snapshot.getValue<User>(User::class.java)
                    Picasso.get().load(user!!.getImage()).placeholder(R.drawable.profile).into(view?.pro_image_profile_frag)
                    view?.profile_fragment_username?.text = user.getUsername()
                    view?.full_name_profile_frag?.text = user.getFullname()
                    view?.bio_profile_frag?.text = user.getBiography()
                }

            }

            override fun onCancelled(error: DatabaseError) {

            }


        })
    }

    override fun onStop() {
        super.onStop()
        val pref = context?.getSharedPreferences("PREFS",Context.MODE_PRIVATE)?.edit()
        pref?.putString("Profileid",firebaseUser.uid)
        pref?.apply()
    }

    override fun onPause() {
        super.onPause()
        val pref = context?.getSharedPreferences("PREFS",Context.MODE_PRIVATE)?.edit()
        pref?.putString("Profileid",firebaseUser.uid)
        pref?.apply()
    }

    override fun onDestroy() {
        super.onDestroy()
        val pref = context?.getSharedPreferences("PREFS",Context.MODE_PRIVATE)?.edit()
        pref?.putString("Profileid",firebaseUser.uid)
        pref?.apply()
    }


}