package skinny.validator

/**
  * Validations factory.
  */
object Validations {

  /**
    * Creates a Validations from a Map value and validation states.
    *
    * @param params params as a Map value
    * @param states validation states
    * @return validations
    */
  def apply(params: Map[String, Any], states: Seq[ValidationState]): Validations = new ValidationsImpl(
    paramsMap = params,
    states = states.map {
      case NewValidation(k: OnlyKeyParamDefinition, validations) =>
        validations.apply(KeyValueParamDefinition(k.key, extractValue(params.get(k.key))))
      case NewValidation(kv: KeyValueParamDefinition, validations) =>
        validations.apply(KeyValueParamDefinition(kv.key, extractValue(kv.value)))
      case done => done
    }
  )

  private[this] def extractValue(value: Any): Any = value match {
    case Some(v) => v
    case None    => null
    case v       => v
  }
}

/**
  * Validations container.
  */
trait Validations {

  /**
    * Params as a Map value.
    */
  val paramsMap: Map[String, Any]

  /**
    * States of validations.
    */
  val states: Seq[ValidationState]

  /**
    * Returns params.
    *
    * @return params
    */
  def params: Parameters = ParametersFromMap(paramsMap)

  /**
    * Returns true if all validations succeeded.
    *
    * @return true/false
    */
  def isSuccess: Boolean = filterFailuresOnly().isEmpty

  /**
    * Returns validation errors.
    *
    * @return errors
    */
  def errors: Errors = Errors(filterErrorsOnly())

  /**
    * Filters successes only.
    *
    * @return successes
    */
  def filterSuccessesOnly(): Seq[ValidationSuccess] = {
    states.filter(_.isInstanceOf[ValidationSuccess]).map(_.asInstanceOf[ValidationSuccess])
  }

  /**
    * Filters failures only.
    *
    * @return failures
    */
  def filterFailuresOnly(): Seq[ValidationFailure] = {
    states.filter(_.isInstanceOf[ValidationFailure]).map(_.asInstanceOf[ValidationFailure])
  }

  /**
    * Filters errors only.
    *
    * @return errors
    */
  def filterErrorsOnly(): Map[String, Seq[Error]] = filterFailuresOnly().groupBy(_.paramDef.key).map {
    case (key, fs) => (key, fs.flatMap(_.errors))
  }

  /**
    * Success event handler.
    *
    * @param f operation
    * @tparam A result type
    * @return projection
    */
  def success[A](f: (Parameters) => A): SuccessesProjection[A] = {
    SuccessesProjection[A](this, ResultsProjection.defaultOnSuccess, ResultsProjection.defaultOnFailures).map(f)
  }

  /**
    * Failure event handler.
    *
    * @param f operation
    * @tparam A result type
    * @return projection
    */
  def failure[A](f: (Parameters, Errors) => A): FailuresProjection[A] = {
    FailuresProjection[A](this, ResultsProjection.defaultOnSuccess, ResultsProjection.defaultOnFailures).map(f)
  }

  /**
    * Returns states as a Seq value.
    *
    * @return states
    */
  def statesAsSeq(): Seq[ValidationState] = states

  /**
    * Returns states as a Map value.
    *
    * @return states
    */
  def statesAsMap(): Map[String, Any] =
    Map(states.map { r =>
      (r.paramDef.key, r.paramDef.value)
    }: _*)

}

/**
  * Default implementation of Validations.
  *
  * @param paramsMap params as a Map value
  * @param states validation states
  */
private class ValidationsImpl(
    override val paramsMap: Map[String, Any],
    override val states: Seq[ValidationState]
) extends Validations
