context("Posting data")

h <- new_handle()

test_that("Post text data", {
  handle_setopt(h, COPYPOSTFIELDS = "moo=moomooo");
  handle_setheaders(h,
    "Content-Type" = "text/moo",
    "Cache-Control" = "no-cache",
    "User-Agent" = "A cow"
  )
  req <- curl_fetch_memory(httpbin("post"), handle = h)
  res <- jsonlite::fromJSON(rawToChar(req$content))

  expect_equal(res$data, "moo=moomooo")
  expect_equal(res$headers$`Content-Type`, "text/moo")
  expect_equal(res$headers$`User-Agent`, "A cow")

  # Using connection interface
  input <- jsonlite::fromJSON(rawToChar(req$content))
  output <- jsonlite::fromJSON(curl(httpbin("post"), handle = h))
  expect_equal(input, output)

  # Using download interface
  tmp <- tempfile()
  curl_download(httpbin("post"), tmp, handle = h)
  txt2 <- readLines(tmp)
  unlink(tmp)
  expect_equal(rawToChar(req$content), paste0(txt2, "\n", collapse=""))
  suppressWarnings(gc())
})

test_that("Change headers", {
  # Default to application/url-encoded
  handle_setheaders(h, "User-Agent" = "Not a cow")
  req <- curl_fetch_memory(httpbin("post"), handle = h)
  res <- jsonlite::fromJSON(rawToChar(req$content))
  expect_equal(res$form$moo, "moomooo")
  expect_equal(res$headers$`User-Agent`, "Not a cow")

})

test_that("Post JSON data", {
  handle_reset(h)
  handle_setopt(h, COPYPOSTFIELDS = jsonlite::toJSON(mtcars));
  handle_setheaders(h, "Content-Type" = "application/json")
  req <- curl_fetch_memory(httpbin("post"), handle = h)
  output <- jsonlite::fromJSON(rawToChar(req$content))

  # Note that httpbin reoders columns alphabetically
  expect_is(output$json, "data.frame")
  expect_equal(sort(names(output$json)), sort(names(mtcars)))
})

test_that("Multipart form post", {
  # Don't reset options manually, curl should figure this out.
  handle_setheaders(h);
  handle_setform(h,
    foo = "blabla",
    bar = charToRaw("boeboe"),
    iris = form_data(serialize(iris, NULL), "data/rda"),
    description = form_file(system.file("DESCRIPTION")),
    logo = form_file(file.path(Sys.getenv("R_DOC_DIR"), "html/logo.jpg"), "image/jpeg")
  )
  req <- curl_fetch_memory(httpbin("post"), handle = h)
  res <- jsonlite::fromJSON(rawToChar(req$content))

  expect_match(res$headers$`Content-Type`, "multipart")
  expect_equal(sort(names(res$files)), c("description", "logo"))
  expect_equal(sort(names(res$form)), c("bar", "foo", "iris"))
})

rm(h)
test_that("GC works", {
  gc()
  expect_equal(total_handles(), 0L)
})

