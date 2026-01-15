import React, { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import ChangeService from "../services/ChangeService";
import { auth } from "../services/auth";
import ChangeSubmitModal from "./ChangeSubmitModal";
import ChangeApproveModal from "./ChangeApproveModal";
import "./changeDetailsPage.css";

const STATUS_COLORS = {
  DRAFT: "#6c757d",
  PENDING_APPROVAL: "#ffc107",
  APPROVED: "#28a745",
  REJECTED: "#dc3545",
  IMPLEMENTED: "#007bff",
};

function ChangeDetailsPage({ showToast }) {
  const { id } = useParams();
  const navigate = useNavigate();

  const user = auth.getUser();
  const role = user?.role;
  const canAct = role === "ENGINEER" || role === "ADMIN";

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [change, setChange] = useState(null);

  const [showSubmit, setShowSubmit] = useState(false);
  const [showApprove, setShowApprove] = useState(false);
  const [actionLoading, setActionLoading] = useState(false);

  const load = async () => {
    setLoading(true);
    setError("");
    try {
      const res = await ChangeService.getChangeById(id);
      setChange(res.data);
    } catch (e) {
      setError(e?.response?.data?.message || e?.response?.data || e?.message || "Failed to load change");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (id) load();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [id]);

  const handleSubmit = async (payload) => {
    if (!canAct) {
      showToast?.("Only ENGINEER/ADMIN can submit changes", "info");
      return;
    }
    try {
      setActionLoading(true);
      await ChangeService.submitChange(id, payload);
      showToast?.("Change submitted for approval", "success");
      setShowSubmit(false);
      await load();
    } catch (e) {
      showToast?.(e?.response?.data?.message || "Failed to submit change", "error");
    } finally {
      setActionLoading(false);
    }
  };

  const handleApprove = async (payload) => {
    if (!canAct) {
      showToast?.("Only ENGINEER/ADMIN can approve changes", "info");
      return;
    }
    try {
      setActionLoading(true);
      await ChangeService.approveChange(id, payload);
      showToast?.(
        payload.status === "APPROVED" ? "Change approved" : "Change rejected",
        payload.status === "APPROVED" ? "success" : "info"
      );
      setShowApprove(false);
      await load();
    } catch (e) {
      showToast?.(e?.response?.data?.message || "Failed to process approval", "error");
    } finally {
      setActionLoading(false);
    }
  };

  const handleImplement = async () => {
    if (!canAct) {
      showToast?.("Only ENGINEER/ADMIN can implement changes", "info");
      return;
    }

    if (change?.status !== "APPROVED") {
      showToast?.("Only approved changes can be implemented", "info");
      return;
    }

    try {
      setActionLoading(true);
      await ChangeService.implementChange(id);
      showToast?.("Change implemented successfully", "success");
      await load();
    } catch (e) {
      showToast?.(e?.response?.data?.message || "Failed to implement change", "error");
    } finally {
      setActionLoading(false);
    }
  };

  if (loading) return <div className="wc-change-details-loading">Loading change details...</div>;
  if (error) return <div className="wc-change-details-error">{String(error)}</div>;
  if (!change) return <div className="wc-change-details-error">Change not found</div>;

  const isPendingApproval = change?.status === "PENDING_APPROVAL";
  const isApproved = change?.status === "APPROVED";
  const isDraft = change?.status === "DRAFT";

  return (
    <div className="wc-change-details-page">
      <div className="wc-change-details-header">
        <button
          className="btn btn-sm btn-outline-secondary"
          onClick={() => navigate("/changes")}
          type="button"
        >
          ← Back to Changes
        </button>
      </div>

      <div className="wc-change-details-card">
        <div className="wc-change-details-top">
          <div>
            <h2 className="wc-change-details-title">Change #{change?.changeNumber || change?.id || id}</h2>
            <p className="wc-change-details-subtitle">{change?.title || "—"}</p>
          </div>
          <span
            className="wc-status-badge-lg"
            style={{ backgroundColor: STATUS_COLORS[change?.status] || "#6c757d" }}
          >
            {change?.status || "—"}
          </span>
        </div>

        <div className="wc-change-meta">
          <div className="wc-meta-item">
            <strong>Type:</strong> {change?.changeType || "—"}
          </div>
          <div className="wc-meta-item">
            <strong>Priority:</strong> {change?.priority || "—"}
          </div>
          <div className="wc-meta-item">
            <strong>Created By:</strong> {change?.createdBy || "—"}
          </div>
          <div className="wc-meta-item">
            <strong>Assigned To:</strong> {change?.assignedTo || "—"}
          </div>
          {change?.dueDate && (
            <div className="wc-meta-item">
              <strong>Due Date:</strong> {new Date(change.dueDate).toLocaleDateString()}
            </div>
          )}
        </div>

        {change?.description && (
          <div className="wc-change-section">
            <h4>Description</h4>
            <p>{change.description}</p>
          </div>
        )}

        {change?.impactAssessment && (
          <div className="wc-change-section">
            <h4>Impact Assessment</h4>
            <p>{change.impactAssessment}</p>
          </div>
        )}

        {change?.affectedPartIds && change.affectedPartIds.length > 0 && (
          <div className="wc-change-section">
            <h4>Affected Parts</h4>
            <p>{change.affectedPartIds.join(", ")}</p>
          </div>
        )}

        {change?.approvals && change.approvals.length > 0 && (
          <div className="wc-change-section">
            <h4>Approvals</h4>
            <table className="wc-approvals-table">
              <thead>
                <tr>
                  <th>Order</th>
                  <th>Approver</th>
                  <th>Status</th>
                  <th>Comments</th>
                </tr>
              </thead>
              <tbody>
                {change.approvals.map((approval, idx) => (
                  <tr key={idx}>
                    <td>#{approval.approvalOrder || idx + 1}</td>
                    <td>{approval.approverId}</td>
                    <td>
                      <span
                        className="wc-approval-badge"
                        style={{
                          backgroundColor:
                            approval.status === "APPROVED"
                              ? "#28a745"
                              : approval.status === "REJECTED"
                              ? "#dc3545"
                              : "#ffc107",
                        }}
                      >
                        {approval.status}
                      </span>
                    </td>
                    <td>{approval.comments || "—"}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>

      {canAct && (
        <div className="wc-change-actions">
          {isDraft && (
            <button
              className="btn btn-primary"
              onClick={() => setShowSubmit(true)}
              disabled={actionLoading}
            >
              📧 Submit for Review
            </button>
          )}

          {isPendingApproval && (
            <button
              className="btn btn-warning"
              onClick={() => setShowApprove(true)}
              disabled={actionLoading}
            >
              ✓ Approve or Reject
            </button>
          )}

          {isApproved && (
            <button className="btn btn-success" onClick={handleImplement} disabled={actionLoading}>
              ⚡ Implement
            </button>
          )}
        </div>
      )}

      {showSubmit && !actionLoading && (
        <ChangeSubmitModal
          changeId={id}
          onSubmit={handleSubmit}
          onClose={() => setShowSubmit(false)}
        />
      )}

      {showApprove && !actionLoading && (
        <ChangeApproveModal
          onSubmit={handleApprove}
          onClose={() => setShowApprove(false)}
        />
      )}
    </div>
  );
}

export default ChangeDetailsPage;
