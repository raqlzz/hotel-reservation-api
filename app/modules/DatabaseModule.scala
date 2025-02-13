package modules

import play.api.{Configuration, Environment}
import play.api.inject.{Binding, Module}

class DatabaseModule extends Module {
  override def bindings(environment: Environment, configuration: Configuration): Seq[Binding[_]] = {
    Seq(
      bind[DatabaseInitializer].toSelf.eagerly()
    )
  }
}
