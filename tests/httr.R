library(curl)
library(httr)

print(parse_url("http://google.com:80/?a=1&b=2#helloworld"))


r <- VERB("PROPFIND", "http://svn.r-project.org/R/tags/",
  add_headers(depth = 1), verbose())
