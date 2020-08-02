package Model

class User {

    private var  username: String = ""
    private var  fullname: String = ""
    private var  biography: String = ""
    private var  image: String = ""
    private var  uid: String = ""

    constructor()

    constructor(username: String, fullname: String, biography: String, image: String, uid: String) {
        this.username = username
        this.fullname = fullname
        this.biography = biography
        this.image = image
        this.uid = uid
    }

    fun getUsername():String
    {
        return username
    }

    fun setUsername(username: String)
    {
        this.username = username
    }
    fun getFullname():String
    {
        return fullname
    }

    fun setFullname(fullname: String)
    {
        this.fullname = fullname
    }
    fun getBiography():String
    {
        return biography
    }

    fun setBiography(biography: String)
    {
        this.biography= biography
    }
    fun getImage():String
    {
        return image
    }

    fun setImage(image: String)
    {
        this.image = image
    }
    fun getUid():String
    {
        return uid
    }

    fun setUid(uid: String)
    {
        this.uid= uid
    }



}