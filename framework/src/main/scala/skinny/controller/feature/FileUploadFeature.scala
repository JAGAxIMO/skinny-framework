package skinny.controller.feature

import skinny.micro.SkinnyMicroServlet
import skinny.micro.contrib.FileUploadSupport

/**
  * File upload feature.
  *
  * When using file upload feature, we cannot use SkinnyController(= serlvet Filter) due to Servlet restriction.
  *
  * {{{
  *   // src/main/scala/controller/FileUploadController.scala
  *
  *   class FileUploadController extends SkinnyServlet with FileUploadFeature {
  *     def form = render("/fileUpload/form")
  *     def submit = {
  *       fileParams.get("file") match {
  *         case Some(file) => println(new String(file.get()))
  *         case None => println("file not found")
  *       }
  *     redirect(url(Controllers.fileUpload.formUrl))
  *   }
  *
  *   // src/main/scala/controller/Controllers.scala
  *
  *   object fileUpload extends FileUploadController with Routes {
  *     val formUrl = get("/fileupload")(form).as('form)
  *     val submitUrl = post("/fileupload/submit")(submit).as('submit)
  *   }
  *   fileUpload.mount(ctx)
  * }}}
  */
trait FileUploadFeature extends FileUploadSupport { self: SkinnyMicroServlet =>

}
