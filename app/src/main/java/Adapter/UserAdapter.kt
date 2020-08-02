package Adapter

import Fragments.HomeFragment
import Fragments.ProfileFragment
import Model.User
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.SearchView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.rgbat.zeechat.AccountSettingsActivity
import com.rgbat.zeechat.R
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.user_item_layout.view.*

class UserAdapter (private var mContext :Context,private var mUser:List<User>,
                   private var isFragment: Boolean = false ) :
    RecyclerView.Adapter<UserAdapter.ViewHolder>()
{

    private var firebaseUser:FirebaseUser? = FirebaseAuth.getInstance().currentUser

    class ViewHolder(@NonNull itemView: View):RecyclerView.ViewHolder(itemView){

        var userNameText:TextView = itemView.findViewById(R.id.user_name_search)
        var userFullNameText:TextView = itemView.findViewById(R.id.user_full_name_search)
        var userProfileImage:CircleImageView= itemView.findViewById(R.id.user_profile_image_search)
        var followButton:Button = itemView.findViewById(R.id.follow_btn_search)

}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val  view = LayoutInflater.from(mContext).inflate(R.layout.user_item_layout,parent,false)
        return UserAdapter.ViewHolder(view)

    }

    override fun getItemCount(): Int {
       return mUser.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val user = mUser[position]

        holder.userFullNameText.text =user.getFullname()
        holder.userNameText.text =user.getUsername()


        Picasso.get().load(user.getImage()).placeholder(R.drawable.profile).into(holder.userProfileImage)

        checkFollowingStatus(user.getUid(),holder.followButton)


        holder.itemView .setOnClickListener {View.OnClickListener{
            

            val pref = mContext .getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit()
            pref.putString("Profileid",user.getUid())
            pref.apply()





            (mContext as FragmentActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container,ProfileFragment()).commit()
       }}




        holder.followButton.setOnClickListener {


            if (holder.followButton.text.toString() == "Follow"){

                firebaseUser?.uid.let { it ->

                    FirebaseDatabase.getInstance().reference.child("Follow").child(it.toString())
                        .child("Following").child(user.getUid())
                        .setValue(true).addOnCompleteListener { task ->

                            if (task.isSuccessful){

                                FirebaseDatabase.getInstance().reference.child("Follow")
                                    .child(user.getUid())
                                    .child("Followers").child(it.toString())
                                    .setValue(true).addOnCompleteListener { task ->

                                        if (task.isSuccessful){


                                        }
                                    }
                            }
                        }

                }





            }

            else{

                firebaseUser?.uid.let { it->

                    FirebaseDatabase.getInstance().reference.child("Follow").child(it.toString())
                        .child("Following").child(user.getUid())
                        .removeValue().addOnCompleteListener { task ->

                            if (task.isSuccessful){

                                FirebaseDatabase.getInstance().reference.child("Follow")
                                    .child(user.getUid())
                                    .child("Followers").child(it.toString())
                                    .removeValue().addOnCompleteListener { task ->

                                        if (task.isSuccessful){


                                        }
                                    }
                            }
                        }

                }



            }
        }


    }


    private fun checkFollowingStatus(uid: String, followButton: Button) {

       val followingRef =  firebaseUser?.uid.let { it1 ->

            FirebaseDatabase.getInstance().reference.child("Follow").child(it1.toString())
                .child("Following")
        }
        followingRef.addValueEventListener(object :ValueEventListener{


            override fun onDataChange(datasnapshot: DataSnapshot) {

                if (datasnapshot.child(uid).exists()){

                    followButton.text = "Following"
                }
                else{

                    followButton.text = "Follow"
                }

            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

}