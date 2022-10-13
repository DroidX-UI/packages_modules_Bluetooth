/*
 * Copyright 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package android.bluetooth;

import android.annotation.IntDef;
import android.annotation.NonNull;
import android.annotation.Nullable;
import android.os.Parcel;
import android.os.Parcelable;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Objects;

/**
 * Represents Bluetooth Audio Policies of a Handsfree (HF) device (if HFP is used)
 * and Call Terminal (CT) device (if BLE Audio is used), which describes the
 * preferences of allowing or disallowing audio based on the use cases. The HF/CT
 * devices shall send objects of this class to send its preference to the AG/CG
 * devices.
 *
 * <p>HF/CT side applications on can use {@link BluetoothDevice#setAudioPolicy}
 * API to set and send a {@link BluetoothAudioPolicy} object containing the
 * preference/policy values. This object will be stored in the memory of HF/CT
 * side, will be send to the AG/CG side using Android Specific AT Commands and will
 * be stored in the AG side memory and database.
 *
 * <p>HF/CT side API {@link BluetoothDevice#getAudioPolicy} can be used to retrieve
 * the stored audio policies currently.
 *
 * <p>Note that the setter APIs of this class will only set the values of the
 * object. To actually set the policies, API {@link BluetoothDevice#setAudioPolicy}
 * must need to be invoked with the {@link BluetoothAudioPolicy} object.
 *
 * <p>Note that any API related to this feature should be used after configuring
 * the support of the AG device and after checking whether the AG device supports
 * this feature or not by invoking {@link BluetoothDevice#getAudioPolicyRemoteSupported}.
 * Only after getting a {@link BluetoothAudioPolicy#FEATURE_SUPPORTED_BY_REMOTE} response
 * from the API should the APIs related to this feature be used.
 *
 *
 * @hide
 */
public final class BluetoothAudioPolicy implements Parcelable {

    /**
     * @hide
    */
    @Retention(RetentionPolicy.SOURCE)
    @IntDef(
        prefix = {"POLICY_"},
        value = {
            POLICY_UNCONFIGURED,
            POLICY_ALLOWED,
            POLICY_NOT_ALLOWED,
        }
    )
    public @interface AudioPolicyValues{}

    /**
     * Audio behavior not configured for the policy.
     *
     * If a policy is set with this value, it means that the policy is not
     * configured with a value yet and should not be used to make any decision.
     * @hide
     */
    public static final int POLICY_UNCONFIGURED = 0;

    /**
     * Audio is preferred by HF device for the policy.
     *
     * If a policy is set with this value, then the HF device will prefer the
     * audio for the policy use case. For example, if the Call Establish audio
     * policy is set with this value, then the HF will prefer the audio
     * during making or picking up a call.
     * @hide
     */
    public static final int POLICY_ALLOWED = 1;

    /**
     * Audio is not preferred by HF device for the policy.
     *
     * If a policy is set with this value, then the HF device will not prefer the
     * audio for the policy use case. For example, if the Call Establish audio
     * policy is set with this value, then the HF will not prefer the audio
     * during making or picking up a call.
     * @hide
     */
    public static final int POLICY_NOT_ALLOWED = 2;

    /**
     * Remote support status of audio policy feature is unknown/unconfigured.
     *
     * @hide
     */
    public static final int FEATURE_UNCONFIGURED_BY_REMOTE = 0;

    /**
     * Remote support status of audio policy feature is supported.
     *
     * @hide
     */
    public static final int FEATURE_SUPPORTED_BY_REMOTE = 1;

    /**
     * Remote support status of audio policy feature is not supported.
     *
     * @hide
     */
    public static final int FEATURE_NOT_SUPPORTED_BY_REMOTE = 2;


    @AudioPolicyValues private final int mCallEstablishPolicy;
    @AudioPolicyValues private final int mConnectingTimePolicy;
    @AudioPolicyValues private final int mInBandRingtonePolicy;

    /**
     * @hide
     */
    public BluetoothAudioPolicy(int callEstablishPolicy,
            int connectingTimePolicy, int inBandRingtonePolicy) {
        mCallEstablishPolicy = callEstablishPolicy;
        mConnectingTimePolicy = connectingTimePolicy;
        mInBandRingtonePolicy = inBandRingtonePolicy;
    }

    /**
     * Get Call pick up audio policy.
     *
     * @return the call pick up audio policy value
     *
     */
    public @AudioPolicyValues int getCallEstablishPolicy() {
        return mCallEstablishPolicy;
    }

    /**
     * Get during connection audio up policy.
     *
     * @return the during connection audio policy value
     *
     */
    public @AudioPolicyValues int getConnectingTimePolicy() {
        return mConnectingTimePolicy;
    }

    /**
     * Get In band ringtone audio up policy.
     *
     * @return the in band ringtone audio policy value
     *
     */
    public @AudioPolicyValues int getInBandRingtonePolicy() {
        return mInBandRingtonePolicy;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("BluetoothAudioPolicy{");
        builder.append("mCallEstablishPolicy: ");
        builder.append(mCallEstablishPolicy);
        builder.append(", mConnectingTimePolicy: ");
        builder.append(mConnectingTimePolicy);
        builder.append(", mInBandRingtonePolicy: ");
        builder.append(mInBandRingtonePolicy);
        builder.append("}");
        return builder.toString();
    }

    /**
     * {@link Parcelable.Creator} interface implementation.
     */
    public static final @android.annotation.NonNull Parcelable.Creator<BluetoothAudioPolicy>
            CREATOR = new Parcelable.Creator<BluetoothAudioPolicy>() {
                @Override
                public BluetoothAudioPolicy createFromParcel(@NonNull Parcel in) {
                    return new BluetoothAudioPolicy(
                            in.readInt(), in.readInt(), in.readInt());
                }

                @Override
                public BluetoothAudioPolicy[] newArray(int size) {
                    return new BluetoothAudioPolicy[size];
                }
            };

    @Override
    public void writeToParcel(@NonNull Parcel out, int flags) {
        out.writeInt(mCallEstablishPolicy);
        out.writeInt(mConnectingTimePolicy);
        out.writeInt(mInBandRingtonePolicy);
    }

    /**
     * @hide
     */
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (o instanceof BluetoothAudioPolicy) {
            BluetoothAudioPolicy other = (BluetoothAudioPolicy) o;
            return (other.mCallEstablishPolicy == mCallEstablishPolicy
                    && other.mConnectingTimePolicy == mConnectingTimePolicy
                    && other.mInBandRingtonePolicy == mInBandRingtonePolicy);
        }
        return false;
    }

    /**
     * Returns a hash representation of this BluetoothCodecConfig
     * based on all the config values.
     *
     * @hide
     */
    @Override
    public int hashCode() {
        return Objects.hash(mCallEstablishPolicy, mConnectingTimePolicy, mInBandRingtonePolicy);
    }

    /**
     * Builder for {@link BluetoothAudioPolicy}.
     * <p> By default, the audio policy values will be set to
     * {@link BluetoothAudioPolicy#POLICY_UNCONFIGURED}.
     */
    public static final class Builder {
        private int mCallEstablishPolicy = POLICY_UNCONFIGURED;
        private int mConnectingTimePolicy = POLICY_UNCONFIGURED;
        private int mInBandRingtonePolicy = POLICY_UNCONFIGURED;

        public Builder() {

        }

        public Builder(@NonNull BluetoothAudioPolicy policies) {
            mCallEstablishPolicy = policies.mCallEstablishPolicy;
            mConnectingTimePolicy = policies.mConnectingTimePolicy;
            mInBandRingtonePolicy = policies.mInBandRingtonePolicy;
        }

        /**
         * Set Call Establish (pick up and answer) policy.
         * <p>This policy is used to determine the audio preference when the
         * HF device makes or answers a call. That is, if this device
         * makes or answers a call, is the audio preferred by HF should be decided
         * by this policy.
         *
         * @return reference to the current object
         */
        public @NonNull Builder setCallEstablishPolicy(
                @AudioPolicyValues int callEstablishPolicy) {
            mCallEstablishPolicy = callEstablishPolicy;
            return this;
        }

        /**
         * Set during connection audio up policy.
         * <p>This policy is used to determine the audio preference when the
         * HF device connects with the AG device. That is, when the
         * HF device gets connected, should the HF become active and get audio
         * is decided by this policy. This also covers the case of during a call.
         * If the HF connects with the AG during an ongoing call, should the call
         * audio be routed to the HF will be decided by this policy.
         *
         * @return reference to the current object
         */
        public @NonNull Builder setConnectingTimePolicy(
                @AudioPolicyValues int connectingTimePolicy) {
            mConnectingTimePolicy = connectingTimePolicy;
            return this;
        }

        /**
         * Set In band ringtone audio up policy.
         * <p>This policy is used to determine the audio preference of the
         * in band ringtone. That is, if there is an incoming call, should the
         * inband ringtone audio be routed to the HF will be decided by this policy.
         *
         * @return reference to the current object
         *
         */
        public @NonNull Builder setInBandRingtonePolicy(
                @AudioPolicyValues int inBandRingtonePolicy) {
            mInBandRingtonePolicy = inBandRingtonePolicy;
            return this;
        }

        /**
         * Build {@link BluetoothAudioPolicy}.
         * @return new BluetoothAudioPolicy object
         */
        public @NonNull BluetoothAudioPolicy build() {
            return new BluetoothAudioPolicy(
                    mCallEstablishPolicy, mConnectingTimePolicy, mInBandRingtonePolicy);
        }
    }
}
