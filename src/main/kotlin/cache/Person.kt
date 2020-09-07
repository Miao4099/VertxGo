package cache

import javax.persistence.*


@Entity
@Table(name = "users", schema = "blog_pro", catalog = "")
@IdClass(PersonPK::class)
open class Person {
    @get:Id
    @get:Column(name = "user_id", nullable = false)
    var userId: String? = null

    @get:Id
    @get:Column(name = "user_name", nullable = false)
    var userName: String? = null

    @get:Basic
    @get:Column(name = "user_url", nullable = true)
    var userUrl: String? = null

    @get:Basic
    @get:Column(name = "user_role", nullable = true)
    var userRole: String? = null

    @get:Basic
    @get:Column(name = "user_avatar", nullable = true)
    var userAvatar: String? = null

    @get:Basic
    @get:Column(name = "user_password", nullable = true)
    var userPassword: String? = null

    @get:Basic
    @get:Column(name = "user_sex", nullable = true)
    var userSex: String? = null

    @get:Basic
    @get:Column(name = "user_type", nullable = true)
    var userType: String? = null

    @get:Basic
    @get:Column(name = "user_state", nullable = true)
    var userState: Int? = null

    @get:Basic
    @get:Column(name = "user_country", nullable = true)
    var userCountry: String? = null

    @get:Basic
    @get:Column(name = "user_province", nullable = true)
    var userProvince: String? = null

    @get:Basic
    @get:Column(name = "user_city", nullable = true)
    var userCity: String? = null

    @get:Basic
    @get:Column(name = "created_time", nullable = true)
    var createdTime: java.sql.Timestamp? = null

    @get:Basic
    @get:Column(name = "updated_time", nullable = true)
    var updatedTime: java.sql.Timestamp? = null

    @get:Basic
    @get:Column(name = "openid", nullable = true)
    var openid: String? = null

    @get:Basic
    @get:Column(name = "wx_name", nullable = true)
    var wxName: String? = null

    @get:Basic
    @get:Column(name = "memo", nullable = true)
    var memo: String? = null


    override fun toString(): String =
        "Entity of type: ${javaClass.name} ( " +
                "userId = $userId " +
                "userName = $userName " +
                "userUrl = $userUrl " +
                "userRole = $userRole " +
                "userAvatar = $userAvatar " +
                "userPassword = $userPassword " +
                "userSex = $userSex " +
                "userType = $userType " +
                "userState = $userState " +
                "userCountry = $userCountry " +
                "userProvince = $userProvince " +
                "userCity = $userCity " +
                "createdTime = $createdTime " +
                "updatedTime = $updatedTime " +
                "openid = $openid " +
                "wxName = $wxName " +
                "memo = $memo " +
                ")"

    // constant value returned to avoid entity inequality to itself before and after it's update/merge
    override fun hashCode(): Int = 42

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as Person

        if (userId != other.userId) return false
        if (userName != other.userName) return false
        if (userUrl != other.userUrl) return false
        if (userRole != other.userRole) return false
        if (userAvatar != other.userAvatar) return false
        if (userPassword != other.userPassword) return false
        if (userSex != other.userSex) return false
        if (userType != other.userType) return false
        if (userState != other.userState) return false
        if (userCountry != other.userCountry) return false
        if (userProvince != other.userProvince) return false
        if (userCity != other.userCity) return false
        if (createdTime != other.createdTime) return false
        if (updatedTime != other.updatedTime) return false
        if (openid != other.openid) return false
        if (wxName != other.wxName) return false
        if (memo != other.memo) return false

        return true
    }

}

class PersonPK : java.io.Serializable {
    @get:Id
    @get:Column(name = "user_id", nullable = false)
    var userId: String? = null

    @get:Id
    @get:Column(name = "user_name", nullable = false)
    var userName: String? = null

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as PersonPK

        if (userId != other.userId) return false
        if (userName != other.userName) return false

        return true
    }

    // constant value returned to avoid entity inequality to itself before and after it's update/merge
    override fun hashCode(): Int = 42

}
