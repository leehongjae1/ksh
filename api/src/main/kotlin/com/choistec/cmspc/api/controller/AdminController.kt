package com.choistec.cmspc.api.controller

import com.choistec.cmspc.api.define.StaticFunc.sha256encode
import com.choistec.cmspc.core.dto.*
import com.choistec.cmspc.core.mysql.dao.AdminDAO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/admins")
class AdminController {
    @Autowired
    lateinit var adminDAO : AdminDAO
    @Autowired
    lateinit var bCryptPasswordEncoder: PasswordEncoder

    /**
     * 관리자 정보 등록
     */
    @PostMapping("")
    fun insert (@RequestBody requestBody:Map<String,Any>) =
            adminDAO.insert(Admin(requestBody[ID].toString()).apply {
                requestBody[PASSWORD].toString().run {
                    password = bCryptPasswordEncoder.encode(this)
                }
                name = requestBody[NAME].toString()
                email = requestBody[EMAIL].toString()
            })

    /**
     * 관리자 정보 호출
     * 들어온 id 와 패스워드가 동일한 값이 하나라도 있으면
     * 레코드 카운트를 리턴한다.
     */
    @GetMapping("")
    fun select (@RequestParam id:String, @RequestParam password:String) = adminDAO.selectPw(id)
            .lastOrNull()
            ?.takeIf {
                /**
                 * bCryptPasswordEncoder(클라이언트가 보낸 비밀번호 ,서버에서 추출한 인코드된 비밀번호)
                 * 로 구성해서 일치하는가를 확인한다.
                 */
                bCryptPasswordEncoder.matches(sha256encode(password),it.password)

            }
            ?.apply {
                adminDAO.selectOne(
                        id
                ).last().let {
                    name = it.name
                    email = it.email
                    hospital_id = it.hospital_id
                }
            }

    /**
     * 관리자 정보 삭제
     */
    @DeleteMapping("")
    fun delete (@RequestParam id:String) = adminDAO.delete(id)

    /**
     * 환경 정보 데이터 호출
     */
    @GetMapping("/data")
    fun data (@RequestParam id:String) = adminDAO.selectEnvironmentData(id).lastOrNull()

}
