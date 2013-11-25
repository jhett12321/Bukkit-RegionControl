package com.featherminecraft.RegionControl.capturableregion;

import com.featherminecraft.RegionControl.Faction;

public class CaptureTimer {

    private CapturableRegion region;

    public CaptureTimer(CapturableRegion cregion) {
        this.region = cregion;
    }

    public void Runnable() {

        long millisecondsremaining = 0;

        Faction influenceOwner = this.region.getInfluenceOwner();
        Faction majorityController = this.region.getMajorityController();
        Float baseInfluence = this.region.getBaseInfluence();

        Float influence = 0F;
        if (influenceOwner != null) {
            influence = this.region.getInfluenceMap().get(influenceOwner);
        }

        if (influenceOwner == null) {
            if (this.region.getInfluenceRate() == 4) {
                millisecondsremaining = (long) ((baseInfluence) / 4 * 1000);
            }

            if (this.region.getInfluenceRate() == 3) {
                millisecondsremaining = (long) ((baseInfluence) / 3 * 1000);
            }

            else if (this.region.getInfluenceRate() == 2) {
                millisecondsremaining = (long) ((baseInfluence) / 2 * 1000);
            }

            else if (this.region.getInfluenceRate() == 1) {
                millisecondsremaining = (long) ((baseInfluence) * 1000);
            }
        }

        else if (influenceOwner == majorityController) {
            if (this.region.getInfluenceRate() == 4) {
                millisecondsremaining = (long) ((baseInfluence - influence) / 4 * 1000);
            }

            if (this.region.getInfluenceRate() == 3) {
                millisecondsremaining = (long) ((baseInfluence - influence) / 3 * 1000);
            }

            else if (this.region.getInfluenceRate() == 2) {
                millisecondsremaining = (long) ((baseInfluence - influence) / 2 * 1000);
            }

            else if (this.region.getInfluenceRate() == 1) {
                millisecondsremaining = (long) ((baseInfluence - influence) * 1000);
            }
        }

        else if (this.region.getInfluenceOwner() != this.region
                .getMajorityController()) {
            if (this.region.getInfluenceRate() == 4) {
                millisecondsremaining = (long) ((baseInfluence + influence) / 4 * 1000);
            }

            if (this.region.getInfluenceRate() == 3) {
                millisecondsremaining = (long) ((baseInfluence + influence) / 3 * 1000);
            }

            else if (this.region.getInfluenceRate() == 2) {
                millisecondsremaining = (long) ((baseInfluence + influence) / 2 * 1000);
            }

            else if (this.region.getInfluenceRate() == 1) {
                millisecondsremaining = (long) ((baseInfluence + influence) * 1000);
            }
        }

        if (millisecondsremaining != 0) {
            Integer seconds = (int) ((millisecondsremaining / 1000) % 60);
            Integer minutes = (int) ((millisecondsremaining / (1000 * 60)));

            this.region.setSecondsToCapture(seconds);
            this.region.setMinutesToCapture(minutes);
        }

        else {
            this.region.setSecondsToCapture(0);
            this.region.setMinutesToCapture(0);
        }
    }
}
