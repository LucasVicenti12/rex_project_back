package com.delice.crm.core.user.domain.entities

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.util.UUID

data class User(
    val uuid: UUID?,
    val login: String? = null,
    val pass: String? = null,
    val name: String? = "",
    val surname: String? = "",
    val email: String? = "",
    val userType: UserType? = UserType.EMPLOYEE,
    val status: UserStatus? = UserStatus.ACTIVE,
    val avatar: String? = "",
    val document: String? = "",
): UserDetails {
    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        return if(this.userType === UserType.OWNER || this.userType === UserType.DEV){
            mutableListOf(
                SimpleGrantedAuthority("ROLE_OWNER"),
                SimpleGrantedAuthority("ROLE_EMPLOYEE")
            )
        }else{
            mutableListOf(SimpleGrantedAuthority("ROLE_EMPLOYEE"))
        }
    }

    override fun getPassword(): String = this.pass.toString()

    override fun getUsername(): String = this.login.toString()
}