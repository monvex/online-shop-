package com.example.routing

import com.example.database.users.User
import com.example.database.users.Users
import com.example.routing.requests.AuthRequest
import com.example.routing.requests.SignUpRequest
import com.example.routing.responses.AuthResponse
import com.example.security.hashing.HashingService
import com.example.security.hashing.SaltedHash
import com.example.security.token.TokenClaim
import com.example.security.token.TokenConfig
import com.example.security.token.TokenService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*


fun Application.configureAuthRouting(
    hashingService: HashingService,
    tokenService: TokenService,
    tokenConfig: TokenConfig
) {
    routing {
        post("/signup") {
            val request = call.receiveNullable<SignUpRequest>() ?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }
            val areFieldsBlank = request.username.isBlank() || request.password.isBlank()
            val isPwTooShort = request.password.length < 8
            if (areFieldsBlank || isPwTooShort) {
                call.respond(HttpStatusCode.Conflict)
                return@post
            }
            val saltedHash = hashingService.generateSaltedHash(request.password)
            val user = User(
                username = request.username,
                password = saltedHash.hash,
                salt = saltedHash.salt,
                role = request.role
            )
            val wasAcknowledged = Users.insert(user)
            if (!wasAcknowledged) {
                call.respond(HttpStatusCode.Conflict)
                return@post
            }

            call.respond(HttpStatusCode.OK)
        }

        post("/signin") {
            val request = call.receiveNullable<AuthRequest>() ?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }

            val user = Users.getUserByUserName(request.username)
            if(user == null) {
                call.respond(HttpStatusCode.Conflict, "No such user in database")
                return@post
            }

            val isValidPassword = hashingService.verify(
                value = request.password,
                saltedHash = SaltedHash(
                    hash = user.password,
                    salt = user.salt
                )
            )
            if(!isValidPassword) {
                call.respond(HttpStatusCode.Conflict, "Incorrect username or password")
                return@post
            }
            val token = tokenService.generate(
                config = tokenConfig,
                TokenClaim(
                    name = "userId",
                    value = user.id.toString()
                ),
                TokenClaim(
                    name = "role",
                    value = user.role
                )
            )

            call.respond(
                status = HttpStatusCode.OK,
                message = AuthResponse(
                    token = token,
                    role = user.role
                )
            )
        }
        authenticate {
            get("authenticate") {//Автоматически проверяет токен
                call.respond(HttpStatusCode.OK)
            }
        }

        authenticate {
            get("secret") {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.getClaim("userId", String::class) //Имя такое же как и при регистрации в блоке TokenClaim
                call.respond(HttpStatusCode.OK, "Your userId is $userId")
            }
        }
    }
}