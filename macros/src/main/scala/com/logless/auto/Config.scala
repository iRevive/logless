package com.logless.auto

import com.typesafe.config.ConfigFactory

/**
  * @author Maksim Ochenashko
  */
private[auto] object Config {

  private val typesafeConfig = fromResource("application.conf").withFallback(fromResource("reference.conf"))

  lazy val format: String = {
    if (typesafeConfig.hasPath("logless.format")) typesafeConfig.getString("logless.format")
    else "$className($body)"
  }

  private def fromResource(name: String) =
    ConfigFactory.parseResources(getClass.getClassLoader, name)
}
