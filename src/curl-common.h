#include <Rinternals.h>
#include <curl/curl.h>
#include <curl/easy.h>
#include <string.h>
#include <stdlib.h>

typedef struct {
  unsigned char *buf;
  size_t size;
} memory;

typedef struct {
  int busy;
  memory content;
  SEXP complete;
  SEXP error;
} multireq;

typedef struct {
  CURL *handle;
  struct curl_httppost *form;
  struct curl_slist *headers;
  memory resheaders;
  multireq multi;
  int refCount;
  int locked;
} reference;

CURL* get_handle(SEXP ptr);
reference* get_ref(SEXP ptr);
void assert(CURLcode res);
void massert(CURLMcode res);
void stop_for_status(CURL *http_handle);
SEXP slist_to_vec(struct curl_slist *slist);
struct curl_slist* vec_to_slist(SEXP vec);
struct curl_httppost* make_form(SEXP form);
void set_form(reference *ref, struct curl_httppost* newform);
void set_headers(reference *ref, struct curl_slist *newheaders);
void reset_resheaders(reference *ref);
void reset_multi(reference *ref);
void clean_handle(reference *ref);
size_t push_disk(void* contents, size_t sz, size_t nmemb, FILE *ctx);
size_t append_buffer(void *contents, size_t sz, size_t nmemb, void *ctx);
CURLcode curl_perform_with_interrupt(CURL *handle);
int pending_interrupt();
SEXP make_handle_response(reference *ref);

