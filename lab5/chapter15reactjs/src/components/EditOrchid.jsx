import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { createOrchid, getOrchid, updateOrchid } from "../services/orchidApi";

const emptyForm = {
  orchidName: "", natural: false, orchidDescription: "",
  orchidCategory: "", attractive: false, orchidURL: ""
};

export default function EditOrchid({ createMode = false }) {
  const { id } = useParams();
  const navigate = useNavigate();
  const [form, setForm] = useState(emptyForm);
  const [error, setError] = useState("");

  useEffect(() => {
    setError("");
    if (createMode) {
      setForm(emptyForm);
    } else if (id) {
      getOrchid(id).then(r => setForm(r.data)).catch(e => setError(e.message));
    }
  }, [createMode, id]);

  const change = (e) => {
    const { name, value, type, checked } = e.target;
    setForm(f => ({ ...f, [name]: type === "checkbox" ? checked : value }));
  };

  const submit = async (e) => {
    e.preventDefault();
    try {
      if (createMode) await createOrchid(form);
      else await updateOrchid(id, form);
      navigate("/");
    } catch (e) { setError(e.message); }
  };

  return (
    <form onSubmit={submit}>
      <h2>{createMode ? "Create Orchid" : "Edit Orchid"}</h2>
      {error && <div className="alert alert-danger">{error}</div>}
      <label className="form-label" htmlFor="orchidName">Name</label>
      <input className="form-control mb-2" id="orchidName" name="orchidName" value={form.orchidName ?? ""} onChange={change} required />
      <label className="form-label" htmlFor="orchidCategory">Category</label>
      <input className="form-control mb-2" id="orchidCategory" name="orchidCategory" value={form.orchidCategory ?? ""} onChange={change} />
      <label className="form-label" htmlFor="orchidDescription">Description</label>
      <textarea className="form-control mb-2" id="orchidDescription" name="orchidDescription" value={form.orchidDescription ?? ""} onChange={change} />
      <label className="form-label" htmlFor="orchidURL">Image URL</label>
      <input className="form-control mb-2" id="orchidURL" name="orchidURL" value={form.orchidURL ?? ""} onChange={change} />
      <div className="form-check mb-2">
        <input className="form-check-input" id="natural" type="checkbox" name="natural" checked={!!form.natural} onChange={change} />
        <label className="form-check-label" htmlFor="natural">Natural</label>
      </div>
      <div className="form-check mb-3">
        <input className="form-check-input" id="attractive" type="checkbox" name="attractive" checked={!!form.attractive} onChange={change} />
        <label className="form-check-label" htmlFor="attractive">Attractive</label>
      </div>
      <button className="btn btn-success" type="submit">Save</button>
    </form>
  );
}
