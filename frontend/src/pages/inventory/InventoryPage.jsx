import { useEffect, useState } from 'react';
import toast from 'react-hot-toast';

import { api } from '../../app/apiClient';
import { PageHeader, Section, LoadingState } from '../../components/ui/Primitives';

export default function InventoryPage() {
  const [items, setItems] = useState(null);
  const [suppliers, setSuppliers] = useState([]);
  const [warehouses, setWarehouses] = useState([]);
  const [tab, setTab] = useState('items');
  const [itemForm, setItemForm] = useState({ sku: '', name: '', category: '', unit: 'pcs', warehouseId: '', stockQuantity: 0, reorderLevel: 0, unitCost: 0, supplierId: '' });
  const [supplierForm, setSupplierForm] = useState({ name: '', contactEmail: '', contactPhone: '', address: '' });
  const [warehouseForm, setWarehouseForm] = useState({ code: '', name: '', location: '', active: true });

  const load = () => {
    api.get('/inventory/items').then((r) => setItems(r.data));
    api.get('/inventory/suppliers').then((r) => setSuppliers(r.data));
    api.get('/inventory/warehouses').then((r) => setWarehouses(r.data));
  };
  useEffect(load, []);

  const createItem = async (e) => { e.preventDefault(); try { await api.post('/inventory/items', itemForm); toast.success('Item added'); load(); } catch {} };
  const createSupplier = async (e) => { e.preventDefault(); try { await api.post('/inventory/suppliers', supplierForm); toast.success('Supplier added'); load(); } catch {} };
  const createWarehouse = async (e) => { e.preventDefault(); try { await api.post('/inventory/warehouses', warehouseForm); toast.success('Warehouse added'); load(); } catch {} };

  return (
    <>
      <PageHeader subtitle="Stores" title="Inventory & consumables" />
      <div className="flex gap-2 mb-6">
        {[['items','Items'],['suppliers','Suppliers'],['warehouses','Warehouses']].map(([k,l]) => (
          <button key={k} onClick={() => setTab(k)} className={tab===k ? 'btn-primary':'btn-outline'}>{l}</button>
        ))}
      </div>

      {tab === 'items' && (
        <>
          <Section title="Add an item" className="mb-6">
            <form onSubmit={createItem} className="grid grid-cols-1 md:grid-cols-4 gap-3">
              <input className="field-input" placeholder="SKU" value={itemForm.sku} onChange={(e) => setItemForm({ ...itemForm, sku: e.target.value })} required />
              <input className="field-input md:col-span-2" placeholder="Name" value={itemForm.name} onChange={(e) => setItemForm({ ...itemForm, name: e.target.value })} required />
              <input className="field-input" placeholder="Category" value={itemForm.category} onChange={(e) => setItemForm({ ...itemForm, category: e.target.value })} />
              <input className="field-input" placeholder="Unit" value={itemForm.unit} onChange={(e) => setItemForm({ ...itemForm, unit: e.target.value })} />
              <input type="number" className="field-input" placeholder="Stock" value={itemForm.stockQuantity} onChange={(e) => setItemForm({ ...itemForm, stockQuantity: Number(e.target.value) })} />
              <input type="number" className="field-input" placeholder="Reorder level" value={itemForm.reorderLevel} onChange={(e) => setItemForm({ ...itemForm, reorderLevel: Number(e.target.value) })} />
              <input type="number" className="field-input" placeholder="Unit cost" value={itemForm.unitCost} onChange={(e) => setItemForm({ ...itemForm, unitCost: Number(e.target.value) })} />
              <select className="field-input" value={itemForm.warehouseId} onChange={(e) => setItemForm({ ...itemForm, warehouseId: e.target.value })}>
                <option value="">Warehouse…</option>
                {warehouses.map((w) => <option key={w.id} value={w.id}>{w.code}</option>)}
              </select>
              <select className="field-input" value={itemForm.supplierId} onChange={(e) => setItemForm({ ...itemForm, supplierId: e.target.value })}>
                <option value="">Supplier…</option>
                {suppliers.map((s) => <option key={s.id} value={s.id}>{s.name}</option>)}
              </select>
              <button className="btn-primary md:col-span-2">Add item</button>
            </form>
          </Section>
          {!items ? <LoadingState /> : (
            <Section title="Items">
              <table className="min-w-full"><thead><tr>
                <th className="table-th">SKU</th><th className="table-th">Name</th><th className="table-th">Stock</th>
                <th className="table-th">Reorder</th><th className="table-th">Cost</th><th className="table-th">Alert</th>
              </tr></thead><tbody>
                {items.map((i) => (
                  <tr key={i.id}>
                    <td className="table-td font-mono">{i.sku}</td>
                    <td className="table-td">{i.name}</td>
                    <td className="table-td">{i.stockQuantity} {i.unit}</td>
                    <td className="table-td">{i.reorderLevel}</td>
                    <td className="table-td">₹ {Number(i.unitCost).toLocaleString('en-IN')}</td>
                    <td className="table-td">{i.lowStock ? '⚠️ Low' : '✅'}</td>
                  </tr>
                ))}
              </tbody></table>
            </Section>
          )}
        </>
      )}

      {tab === 'suppliers' && (
        <>
          <Section title="Add a supplier" className="mb-6">
            <form onSubmit={createSupplier} className="grid grid-cols-1 md:grid-cols-4 gap-3">
              <input className="field-input md:col-span-2" placeholder="Name" value={supplierForm.name} onChange={(e) => setSupplierForm({ ...supplierForm, name: e.target.value })} required />
              <input className="field-input" placeholder="Email" value={supplierForm.contactEmail} onChange={(e) => setSupplierForm({ ...supplierForm, contactEmail: e.target.value })} />
              <input className="field-input" placeholder="Phone" value={supplierForm.contactPhone} onChange={(e) => setSupplierForm({ ...supplierForm, contactPhone: e.target.value })} />
              <input className="field-input md:col-span-3" placeholder="Address" value={supplierForm.address} onChange={(e) => setSupplierForm({ ...supplierForm, address: e.target.value })} />
              <button className="btn-primary">Add supplier</button>
            </form>
          </Section>
          <Section title="Suppliers">
            <ul className="text-sm space-y-2">
              {suppliers.map((s) => <li key={s.id} className="py-2 border-b border-ink-100 dark:border-ink-800 flex justify-between"><span>{s.name}</span><span className="text-ink-500 dark:text-ink-300">{s.contactEmail || '—'}</span></li>)}
            </ul>
          </Section>
        </>
      )}

      {tab === 'warehouses' && (
        <>
          <Section title="Add a warehouse" className="mb-6">
            <form onSubmit={createWarehouse} className="grid grid-cols-1 md:grid-cols-4 gap-3">
              <input className="field-input" placeholder="Code" value={warehouseForm.code} onChange={(e) => setWarehouseForm({ ...warehouseForm, code: e.target.value })} required />
              <input className="field-input md:col-span-2" placeholder="Name" value={warehouseForm.name} onChange={(e) => setWarehouseForm({ ...warehouseForm, name: e.target.value })} required />
              <input className="field-input" placeholder="Location" value={warehouseForm.location} onChange={(e) => setWarehouseForm({ ...warehouseForm, location: e.target.value })} />
              <button className="btn-primary">Add warehouse</button>
            </form>
          </Section>
          <Section title="Warehouses">
            <ul className="text-sm space-y-2">
              {warehouses.map((w) => <li key={w.id} className="py-2 border-b border-ink-100 dark:border-ink-800 flex justify-between"><span className="font-mono">{w.code}</span><span>{w.name}</span></li>)}
            </ul>
          </Section>
        </>
      )}
    </>
  );
}
