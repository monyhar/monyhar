// Copyright 2015 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

#include "components/data_reduction_proxy/core/browser/data_store_impl.h"

#include <stdint.h>

#include "base/files/file_util.h"
#include "base/logging.h"
#include "base/memory/ref_counted.h"
#include "base/metrics/histogram_macros.h"
#include "components/data_reduction_proxy/proto/data_store.pb.h"
#include "third_party/leveldatabase/env_monyhar.h"
#include "third_party/leveldatabase/leveldb_chrome.h"
#include "third_party/leveldatabase/src/include/leveldb/db.h"
#include "third_party/leveldatabase/src/include/leveldb/options.h"
#include "third_party/leveldatabase/src/include/leveldb/status.h"
#include "third_party/leveldatabase/src/include/leveldb/write_batch.h"

namespace {

const base::FilePath::CharType kDBName[] =
    FILE_PATH_LITERAL("data_reduction_proxy_leveldb");

data_reduction_proxy::DataStore::Status LevelDbToDRPStoreStatus(
    leveldb::Status leveldb_status) {
  if (leveldb_status.ok())
    return data_reduction_proxy::DataStore::Status::OK;
  if (leveldb_status.IsNotFound())
    return data_reduction_proxy::DataStore::Status::NOT_FOUND;
  if (leveldb_status.IsCorruption())
    return data_reduction_proxy::DataStore::Status::CORRUPTED;
  if (leveldb_status.IsIOError())
    return data_reduction_proxy::DataStore::Status::IO_ERROR;

  return data_reduction_proxy::DataStore::Status::MISC_ERROR;
}

}  // namespace

namespace data_reduction_proxy {

DataStoreImpl::DataStoreImpl(const base::FilePath& profile_path)
    : profile_path_(profile_path) {
  sequence_checker_.DetachFromSequence();
}

DataStoreImpl::~DataStoreImpl() {
  DCHECK(sequence_checker_.CalledOnValidSequence());
}

void DataStoreImpl::InitializeOnDBThread() {
  DCHECK(sequence_checker_.CalledOnValidSequence());
  DCHECK(!db_);

  DataStore::Status status = OpenDB();
  if (status == CORRUPTED)
    RecreateDB();
}

DataStore::Status DataStoreImpl::Get(base::StringPiece key,
                                     std::string* value) {
  DCHECK(sequence_checker_.CalledOnValidSequence());

  if (!db_)
    return MISC_ERROR;

  leveldb::ReadOptions read_options;
  read_options.verify_checksums = true;
  leveldb::Slice slice(key.data(), key.size());
  leveldb::Status status = db_->Get(read_options, slice, value);
  if (status.IsCorruption())
    RecreateDB();

  return LevelDbToDRPStoreStatus(status);
}

DataStore::Status DataStoreImpl::Put(
    const std::map<std::string, std::string>& map) {
  DCHECK(sequence_checker_.CalledOnValidSequence());

  if (!db_)
    return MISC_ERROR;

  leveldb::WriteBatch batch;
  for (const auto& iter : map) {
    batch.Put(iter.first, iter.second);
  }

  leveldb::WriteOptions write_options;
  leveldb::Status status = db_->Write(write_options, &batch);
  if (status.IsCorruption())
    RecreateDB();

  return LevelDbToDRPStoreStatus(status);
}

DataStore::Status DataStoreImpl::Delete(base::StringPiece key) {
  DCHECK(sequence_checker_.CalledOnValidSequence());

  if (!db_)
    return MISC_ERROR;

  leveldb::Slice slice(key.data(), key.size());
  leveldb::WriteOptions write_options;
  leveldb::Status status = db_->Delete(write_options, slice);
  if (status.IsCorruption())
    RecreateDB();

  return LevelDbToDRPStoreStatus(status);
}

DataStore::Status DataStoreImpl::OpenDB() {
  DCHECK(sequence_checker_.CalledOnValidSequence());

  leveldb_env::Options options;
  options.create_if_missing = true;
  options.paranoid_checks = true;
  // Deletes to buckets not found are stored in the log. Use a new log so that
  // these log entries are deleted.
  options.reuse_logs = false;
  std::string db_name = profile_path_.Append(kDBName).AsUTF8Unsafe();
  db_.reset();
  Status status =
      LevelDbToDRPStoreStatus(leveldb_env::OpenDB(options, db_name, &db_));
  UMA_HISTOGRAM_ENUMERATION("DataReductionProxy.LevelDBOpenStatus", status,
                            STATUS_MAX);

  if (status != OK)
    LOG(ERROR) << "Failed to open Data Reduction Proxy DB: " << status;

  if (db_) {
    leveldb::Range range;
    uint64_t size;
    // We try to capture the size of the entire DB by using the highest and
    // lowest keys.
    range.start = "";
    range.limit = "z";  // Keys starting with 'z' will not be included.
    db_->GetApproximateSizes(&range, 1, &size);
    UMA_HISTOGRAM_MEMORY_KB("DataReductionProxy.LevelDBSize", size / 1024);
  }

  return status;
}

DataStore::Status DataStoreImpl::RecreateDB() {
  DCHECK(sequence_checker_.CalledOnValidSequence());

  db_.reset();
  const base::FilePath db_path = profile_path_.Append(kDBName);
  leveldb::Status s = leveldb_chrome::DeleteDB(db_path, leveldb::Options());
  if (!s.ok())
    return LevelDbToDRPStoreStatus(s);

  return OpenDB();
}

}  // namespace data_reduction_proxy
