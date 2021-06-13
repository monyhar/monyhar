// Copyright 2018 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

#ifndef FUCHSIA_RUNNERS_CAST_FAKE_APPLICATION_CONFIG_MANAGER_H_
#define FUCHSIA_RUNNERS_CAST_FAKE_APPLICATION_CONFIG_MANAGER_H_

#include <map>
#include <string>
#include <vector>

#include "base/macros.h"
#include "fuchsia/fidl/monyhar/cast/cpp/fidl.h"
#include "url/gurl.h"

// Test cast.ApplicationConfigManager implementation which maps a test Cast
// AppId to a URL.
class FakeApplicationConfigManager
    : public monyhar::cast::ApplicationConfigManager {
 public:
  // Default agent url used for all applications.
  static const char kFakeAgentUrl[];

  FakeApplicationConfigManager();
  ~FakeApplicationConfigManager() override;

  // Creates a config for a dummy application with the specified |id| and |url|.
  // Callers should updated the returned config as necessary and then register
  // the app by calling AddAppConfig().
  static monyhar::cast::ApplicationConfig CreateConfig(const std::string& id,
                                                        const GURL& url);

  // Adds |app_config| to the list of apps.
  void AddAppConfig(monyhar::cast::ApplicationConfig app_config);

  // Associates a Cast application |id| with the |url|.
  void AddApp(const std::string& id, const GURL& url);

  // monyhar::cast::ApplicationConfigManager interface.
  void GetConfig(std::string id, GetConfigCallback config_callback) override;

 private:
  std::map<std::string, monyhar::cast::ApplicationConfig> id_to_config_;

  DISALLOW_COPY_AND_ASSIGN(FakeApplicationConfigManager);
};

#endif  // FUCHSIA_RUNNERS_CAST_FAKE_APPLICATION_CONFIG_MANAGER_H_
