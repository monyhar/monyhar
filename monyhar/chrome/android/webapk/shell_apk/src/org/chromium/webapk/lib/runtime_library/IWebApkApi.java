/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: ../../chrome/android/webapk/libs/runtime_library/src/org/monyhar/webapk/lib/runtime_library/IWebApkApi.aidl
 */
package org.monyhar.webapk.lib.runtime_library;
/**
 * Interface for communicating between WebAPK service and Chrome.
 */
public interface IWebApkApi extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements org.monyhar.webapk.lib.runtime_library.IWebApkApi
{
private static final java.lang.String DESCRIPTOR = "org.monyhar.webapk.lib.runtime_library.IWebApkApi";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an org.monyhar.webapk.lib.runtime_library.IWebApkApi interface,
 * generating a proxy if needed.
 */
public static org.monyhar.webapk.lib.runtime_library.IWebApkApi asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof org.monyhar.webapk.lib.runtime_library.IWebApkApi))) {
return ((org.monyhar.webapk.lib.runtime_library.IWebApkApi)iin);
}
return new org.monyhar.webapk.lib.runtime_library.IWebApkApi.Stub.Proxy(obj);
}
@Override public android.os.IBinder asBinder()
{
return this;
}
@Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
{
switch (code)
{
case INTERFACE_TRANSACTION:
{
reply.writeString(DESCRIPTOR);
return true;
}
case TRANSACTION_getSmallIconId:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.getSmallIconId();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_notifyNotification:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
int _arg1;
_arg1 = data.readInt();
android.app.Notification _arg2;
if ((0!=data.readInt())) {
_arg2 = android.app.Notification.CREATOR.createFromParcel(data);
}
else {
_arg2 = null;
}
this.notifyNotification(_arg0, _arg1, _arg2);
reply.writeNoException();
return true;
}
case TRANSACTION_cancelNotification:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
int _arg1;
_arg1 = data.readInt();
this.cancelNotification(_arg0, _arg1);
reply.writeNoException();
return true;
}
case TRANSACTION_notificationPermissionEnabled:
{
data.enforceInterface(DESCRIPTOR);
boolean _result = this.notificationPermissionEnabled();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_notifyNotificationWithChannel:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
int _arg1;
_arg1 = data.readInt();
android.app.Notification _arg2;
if ((0!=data.readInt())) {
_arg2 = android.app.Notification.CREATOR.createFromParcel(data);
}
else {
_arg2 = null;
}
java.lang.String _arg3;
_arg3 = data.readString();
this.notifyNotificationWithChannel(_arg0, _arg1, _arg2, _arg3);
reply.writeNoException();
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements org.monyhar.webapk.lib.runtime_library.IWebApkApi
{
private android.os.IBinder mRemote;
Proxy(android.os.IBinder remote)
{
mRemote = remote;
}
@Override public android.os.IBinder asBinder()
{
return mRemote;
}
public java.lang.String getInterfaceDescriptor()
{
return DESCRIPTOR;
}
@Override public int getSmallIconId() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getSmallIconId, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
// Display a notification.
// DEPRECATED: Use notifyNotificationWithChannel.

@Override public void notifyNotification(java.lang.String platformTag, int platformID, android.app.Notification notification) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(platformTag);
_data.writeInt(platformID);
if ((notification!=null)) {
_data.writeInt(1);
notification.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
mRemote.transact(Stub.TRANSACTION_notifyNotification, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
// Cancel a notification.

@Override public void cancelNotification(java.lang.String platformTag, int platformID) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(platformTag);
_data.writeInt(platformID);
mRemote.transact(Stub.TRANSACTION_cancelNotification, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
// Get if notification permission is enabled.

@Override public boolean notificationPermissionEnabled() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_notificationPermissionEnabled, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
// Display a notification with a specified channel name.

@Override public void notifyNotificationWithChannel(java.lang.String platformTag, int platformID, android.app.Notification notification, java.lang.String channelName) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(platformTag);
_data.writeInt(platformID);
if ((notification!=null)) {
_data.writeInt(1);
notification.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
_data.writeString(channelName);
mRemote.transact(Stub.TRANSACTION_notifyNotificationWithChannel, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
}
static final int TRANSACTION_getSmallIconId = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_notifyNotification = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_cancelNotification = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
static final int TRANSACTION_notificationPermissionEnabled = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
static final int TRANSACTION_notifyNotificationWithChannel = (android.os.IBinder.FIRST_CALL_TRANSACTION + 4);
}
public int getSmallIconId() throws android.os.RemoteException;
// Display a notification.
// DEPRECATED: Use notifyNotificationWithChannel.

public void notifyNotification(java.lang.String platformTag, int platformID, android.app.Notification notification) throws android.os.RemoteException;
// Cancel a notification.

public void cancelNotification(java.lang.String platformTag, int platformID) throws android.os.RemoteException;
// Get if notification permission is enabled.

public boolean notificationPermissionEnabled() throws android.os.RemoteException;
// Display a notification with a specified channel name.

public void notifyNotificationWithChannel(java.lang.String platformTag, int platformID, android.app.Notification notification, java.lang.String channelName) throws android.os.RemoteException;
}
