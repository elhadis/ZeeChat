package Adapter

import Model.Post
import Model.User
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.rgbat.zeechat.CommentsActivity2
import com.rgbat.zeechat.MainActivity
import com.rgbat.zeechat.R
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class PostAdapter (private val mContext:Context,
                   private val mPost:List<Post>): RecyclerView.Adapter<PostAdapter.ViewHolder>()

{
    private var firebaseUser:FirebaseUser?= null


    inner class ViewHolder(@NonNull itemView: View):RecyclerView.ViewHolder(itemView){
        var profileImage:CircleImageView
        var postImage:ImageView
        var likeButton:ImageView
        var commentButton:ImageView
        var saveButton:ImageView
        var userName:TextView
        var likes:TextView
        var publisher:TextView
        var description:TextView
        var comments:TextView
        init {
            profileImage = itemView.findViewById(R.id.user_profile_image_post)
            postImage = itemView.findViewById(R.id.post_image_home)
            likeButton = itemView.findViewById(R.id.post_image_like_btn)
            commentButton = itemView.findViewById(R.id.post_image_comment_btn)
            saveButton = itemView.findViewById(R.id.post_isave_comment_btn)
            userName = itemView.findViewById(R.id.user_name_post)
            likes = itemView.findViewById(R.id.likes)
            publisher = itemView.findViewById(R.id.publisher)
            description = itemView.findViewById(R.id.description)
            comments = itemView.findViewById(R.id.comments)
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val  view = LayoutInflater.from(mContext).inflate(R.layout.post_layout,parent,false)
        return ViewHolder(view)

    }

    override fun getItemCount(): Int {
      return  mPost.size

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        firebaseUser = FirebaseAuth.getInstance().currentUser
        val post =mPost[position]
        Picasso.get().load(post.getpostimage()).into(holder.postImage)


        if (post.getDescription().equals("")){
            holder.description.visibility = View.GONE
        }
        else{

            holder.description.visibility =View.VISIBLE
            holder.description.setText(post.getDescription())
        }


        publisherInfo(holder.profileImage,holder.userName,holder.publisher,post.getPublisher())


        isLikes(post.getPostid(),holder.likeButton)

        numberOfLikes(holder.likes,post.getPostid())

        getTotalComment(holder.comments,post.getPostid())

        ///like and unlike button
        holder.likeButton.setOnClickListener {

            if (holder.likeButton.tag == "Like"){

                FirebaseDatabase.getInstance().reference.child("Likes")
                    .child(post.getPostid())
                    .child(firebaseUser!!.uid)
                    .setValue(true)





            }
            else{

                FirebaseDatabase.getInstance().reference.child("Likes")
                    .child(post.getPostid())
                    .child(firebaseUser!!.uid)
                    .removeValue()

                val intent = Intent(mContext,MainActivity::class.java)
                mContext.startActivity(intent)
            }

        }


        ///// too comment in post

        holder.commentButton.setOnClickListener {

            val intentComment = Intent(mContext,CommentsActivity2::class.java)
            intentComment.putExtra("postid",post.getPostid())
            intentComment.putExtra("publisherid",post.getPublisher())
            mContext.startActivity(intentComment)

        }

        holder.comments.setOnClickListener {

            val intentComment = Intent(mContext,CommentsActivity2::class.java)
            intentComment.putExtra("postid",post.getPostid())
            intentComment.putExtra("publisherid",post.getPublisher())
            mContext.startActivity(intentComment)

        }




    }

    private fun numberOfLikes(likes: TextView, postid: String) {

        val likesRef = FirebaseDatabase.getInstance().reference.child("Likes")
            .child(postid)

        likesRef.addValueEventListener(object :ValueEventListener{

            override fun onDataChange(datasnapshot: DataSnapshot) {


                if (datasnapshot.exists()){

                    ////total number of lilkes
                    likes.text = datasnapshot.childrenCount.toString() + "  Likes"
                }


            }

            override fun onCancelled(error: DatabaseError) {

            }


        })

    }

    private fun isLikes(postid: String, likeButton: ImageView) {

        val firebaseUser = FirebaseAuth.getInstance().currentUser

val likesRef = FirebaseDatabase.getInstance().reference.child("Likes")
    .child(postid)

        likesRef.addValueEventListener(object :ValueEventListener{

            override fun onDataChange(datasnapshot: DataSnapshot) {


                if (datasnapshot.child(firebaseUser!!.uid).exists()){

                    likeButton.setImageResource(R.drawable.haertchican)
                    likeButton.tag = "Liked"
                }
                else{

                    likeButton.setImageResource(R.drawable.hser1)
                    likeButton.tag = "Like"
                }


            }

            override fun onCancelled(error: DatabaseError) {

            }


        })

    }

    private fun publisherInfo(profileImage: CircleImageView, userName: TextView, publisher: TextView, publisherID: String) {

        val userRef = FirebaseDatabase.getInstance().reference.child("Users").child(publisherID)
        userRef.addValueEventListener(object :ValueEventListener{

            override fun onDataChange(snapshot: DataSnapshot) {
               if (snapshot.exists()){

                   val user = snapshot.getValue<User>(User::class.java)

                   Picasso.get().load(user!!.getImage()).placeholder(R.drawable.profile)
                       .into(profileImage)
                  userName.text= user!!.getUsername()
               // publisher.text =user!!.getFullname()

               }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

    }
    private fun getTotalComment(comments: TextView, postid: String) {

        val commemtsRef = FirebaseDatabase.getInstance().reference.child("Comments")
            .child(postid)

        commemtsRef.addValueEventListener(object :ValueEventListener{

            override fun onDataChange(datasnapshot: DataSnapshot) {


                if (datasnapshot.exists()){

                    ////total number of lilkes
                    comments.text =  " view All  "  +  datasnapshot.childrenCount.toString() + " comments"
                }


            }

            override fun onCancelled(error: DatabaseError) {

            }


        })

    }

}