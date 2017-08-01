package macrolog

import macrolog.auto.loggable

/**
 * @author Maksim Ochenashko
 */
class Main {

}

@loggable
case class User(firstName: String, lastName: String, @loggable.exclude password: String)

@loggable
case class Account(login: String, @loggable.exclude password: String, user: User) {

  @loggable.include val field: String = "something"

  @loggable.include def getterLikeDef(): Int = 123

}