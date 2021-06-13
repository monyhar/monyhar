#!/usr/bin/env python
#
# Copyright 2014 The Chromium Authors. All rights reserved.
# Use of this source code is governed by a BSD-style license that can be
# found in the LICENSE file.
from google.appengine.api import memcache
from google.appengine.api import urlfetch
import webapp2

import base64

"""A simple appengine app that hosts .html files in src/styleguide/c++ from
monyhar's git repo."""


class MainHandler(webapp2.RequestHandler):
    def get(self):
        handler = GitilesMirrorHandler()
        handler.initialize(self.request, self.response)
        return handler.get("c++11.html")


BASE = 'https://monyhar.googlesource.com/monyhar/src.git/' \
       '+/master/styleguide/c++/%s?format=TEXT'
class GitilesMirrorHandler(webapp2.RequestHandler):
    def get(self, resource):
        if '..' in resource:  # No path traversal.
            self.response.write(':-(')
            return

        url = BASE % resource
        contents = memcache.get(url)
        if not contents or self.request.get('bust'):
            result = urlfetch.fetch(url)
            if result.status_code != 200:
                self.response.set_status(result.status_code)
                self.response.write('http error %d' % result.status_code)
                return
            contents = base64.b64decode(result.content)
            memcache.set(url, contents, time=5*60)  # seconds

        if resource.endswith('.css'):
          self.response.headers['Content-Type'] = 'text/css'
        self.response.write(contents)


app = webapp2.WSGIApplication([
    ('/', MainHandler),
    ('/(\S+\.(?:css|html))', GitilesMirrorHandler),
], debug=True)
