package Adapter

import Model.Comments
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.rgbat.zeechat.R
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.comments_layout.view.*

class CommentsAdapter(private val mContext: Context,private val  mCommentList:
MutableList<Comments>) : RecyclerView.Adapter<CommentsAdapter.ViewHolder>() {



    abstract class ViewHolder (@NonNull itemView: View): RecyclerView.ViewHolder(itemView) {

       var profileImage: CircleImageView?= null
        var userNameTex:TextView?= null
        var commentTex:TextView?= null

        init {
            profileImage =itemView.findViewById(R.id.user_profile_image_comment)
            userNameTex=itemView.findViewById(R.id.user_name_comment)
            commentTex =itemView.findViewById(R.id.comment_comment)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        TODO("Not yet implemented")
    }




}