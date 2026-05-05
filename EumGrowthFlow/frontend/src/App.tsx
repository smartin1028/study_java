import { useCallback, useState } from 'react';
import ReactFlow, {
  MiniMap,
  Controls,
  Background,
  useNodesState,
  useEdgesState,
  addEdge,
  BackgroundVariant,
} from 'reactflow';
import 'reactflow/dist/style.css';

import AIAgentNode from './components/nodes/AIAgentNode';
import Sidebar from './components/Sidebar';
import NodeConfigPanel from './components/NodeConfigPanel';
import ExecutionPanel from './components/ExecutionPanel';
import ChatInterface from './components/ChatInterface';
import PasswordModal from './components/PasswordModal';
import { workflowExecutor } from './services/workflowExecutor';
import { downloadEncryptedWorkflow, loadEncryptedWorkflow } from './utils/encryption';
import './App.css';

const nodeTypes = {
  aiAgent: AIAgentNode,
};

const initialNodes = [
  {
    id: '1',
    type: 'aiAgent',
    position: { x: 250, y: 100 },
    data: {
      label: 'Start Node',
      agentType: 'input',
      description: '워크플로우 시작점'
    },
  },
];

const initialEdges: any[] = [];

function App() {
  const [nodes, setNodes, onNodesChange] = useNodesState(initialNodes);
  const [edges, setEdges, onEdgesChange] = useEdgesState(initialEdges);
  const [selectedNode, setSelectedNode] = useState<any>(null);
  const [executionResults, setExecutionResults] = useState<any[]>([]);
  const [isRunning, setIsRunning] = useState(false);
  const [showResults, setShowResults] = useState(false);
  const [showChat, setShowChat] = useState(false);
  const [chatConfig, setChatConfig] = useState<any>(null);
  const [showPasswordModal, setShowPasswordModal] = useState(false);
  const [passwordMode, setPasswordMode] = useState<'save' | 'load'>('save');
  const [pendingFile, setPendingFile] = useState<File | null>(null);

  const onConnect = useCallback(
    (params: any) => setEdges((eds) => addEdge(params, eds)),
    [setEdges]
  );

  const onNodeClick = useCallback((_event: any, node: any) => {
    setSelectedNode(node);
  }, []);

  const onPaneClick = useCallback(() => {
    setSelectedNode(null);
  }, []);

  const handleSaveNodeConfig = useCallback((config: any) => {
    setNodes((nds) =>
      nds.map((node) => {
        if (node.id === config.id) {
          return {
            ...node,
            data: {
              ...node.data,
              label: config.label,
              prompt: config.prompt,
              apiUrl: config.apiUrl,
              apiMethod: config.apiMethod,
              apiKeyName: config.apiKeyName,
              model: config.model,
            },
          };
        }
        return node;
      })
    );
  }, [setNodes]);

  const onDragOver = useCallback((event: React.DragEvent) => {
    event.preventDefault();
    event.dataTransfer.dropEffect = 'move';
  }, []);

  const onDrop = useCallback(
    (event: React.DragEvent) => {
      event.preventDefault();

      const type = event.dataTransfer.getData('application/reactflow');
      const label = event.dataTransfer.getData('label');
      const agentType = event.dataTransfer.getData('agentType');
      const description = event.dataTransfer.getData('description');

      const position = {
        x: event.clientX - 250,
        y: event.clientY - 100,
      };

      const newNode = {
        id: `${Date.now()}`,
        type,
        position,
        data: { label, agentType, description },
      };

      setNodes((nds) => nds.concat(newNode));
    },
    [setNodes]
  );

  const onSave = useCallback(() => {
    const flow = {
      nodes,
      edges,
    };
    localStorage.setItem('workflow', JSON.stringify(flow));
    alert('워크플로우가 저장되었습니다!');
  }, [nodes, edges]);

  const onLoad = useCallback(() => {
    const flowData = localStorage.getItem('workflow');
    if (flowData) {
      const flow = JSON.parse(flowData);
      setNodes(flow.nodes || []);
      setEdges(flow.edges || []);
      alert('워크플로우가 로드되었습니다!');
    } else {
      alert('저장된 워크플로우가 없습니다.');
    }
  }, [setNodes, setEdges]);

  const onClear = useCallback(() => {
    setNodes([]);
    setEdges([]);
  }, [setNodes, setEdges]);

  const onExecute = useCallback(async () => {
    setIsRunning(true);
    setShowResults(true);
    setExecutionResults([]);

    try {
      const results = await workflowExecutor.executeWorkflow(nodes, edges);
      setExecutionResults(results);
    } catch (error: any) {
      setExecutionResults([
        {
          nodeId: 'error',
          success: false,
          error: error.message,
        },
      ]);
    } finally {
      setIsRunning(false);
    }
  }, [nodes, edges]);

  const handleOpenChat = useCallback(() => {
    if (selectedNode) {
      setChatConfig({
        apiUrl: selectedNode.data.apiUrl,
        apiMethod: selectedNode.data.apiMethod,
        apiKeyName: selectedNode.data.apiKeyName,
        model: selectedNode.data.model,
      });
      setShowChat(true);
      setSelectedNode(null);
    }
  }, [selectedNode]);

  // 암호화 저장
  const handleEncryptedSave = useCallback(() => {
    setPasswordMode('save');
    setShowPasswordModal(true);
  }, []);

  const handlePasswordConfirm = useCallback((password: string) => {
    if (passwordMode === 'save') {
      try {
        downloadEncryptedWorkflow(nodes, edges, password);
        alert('🔒 암호화된 워크플로우가 다운로드되었습니다!');
      } catch (error: any) {
        alert(`❌ 저장 실패: ${error.message}`);
      }
    } else if (passwordMode === 'load' && pendingFile) {
      loadEncryptedWorkflow(pendingFile, password)
        .then(({ nodes: loadedNodes, edges: loadedEdges }) => {
          setNodes(loadedNodes);
          setEdges(loadedEdges);
          alert('✅ 암호화된 워크플로우가 로드되었습니다!');
        })
        .catch((error: any) => {
          alert(`❌ 불러오기 실패: ${error.message}`);
        })
        .finally(() => {
          setPendingFile(null);
        });
    }
    setShowPasswordModal(false);
  }, [passwordMode, nodes, edges, pendingFile, setNodes, setEdges]);

  // 암호화 파일 불러오기
  const handleEncryptedLoad = useCallback(() => {
    const input = document.createElement('input');
    input.type = 'file';
    input.accept = '.eum';
    input.onchange = (e: any) => {
      const file = e.target.files[0];
      if (file) {
        setPendingFile(file);
        setPasswordMode('load');
        setShowPasswordModal(true);
      }
    };
    input.click();
  }, []);

  return (
    <div style={{ width: '100vw', height: '100vh', display: 'flex' }}>
      <Sidebar />
      <div style={{ flexGrow: 1, position: 'relative' }}>
        <div style={{
          position: 'absolute',
          top: 10,
          right: 10,
          zIndex: 4,
          display: 'flex',
          gap: '10px'
        }}>
          <button onClick={onExecute} style={executeButtonStyle} disabled={isRunning}>
            {isRunning ? '⏳ 실행 중...' : '▶️ 실행'}
          </button>
          <button onClick={onSave} style={buttonStyle}>저장</button>
          <button onClick={onLoad} style={buttonStyle}>불러오기</button>
          <button onClick={handleEncryptedSave} style={encryptedButtonStyle}>🔒 암호화 저장</button>
          <button onClick={handleEncryptedLoad} style={encryptedButtonStyle}>🔓 암호화 불러오기</button>
          <button onClick={onClear} style={buttonStyle}>초기화</button>
        </div>
        <ReactFlow
          nodes={nodes}
          edges={edges}
          onNodesChange={onNodesChange}
          onEdgesChange={onEdgesChange}
          onConnect={onConnect}
          onDrop={onDrop}
          onDragOver={onDragOver}
          onNodeClick={onNodeClick}
          onPaneClick={onPaneClick}
          nodeTypes={nodeTypes}
          fitView
        >
          <Controls />
          <MiniMap />
          <Background variant={BackgroundVariant.Dots} gap={12} size={1} />
        </ReactFlow>
      </div>

      {selectedNode && !showChat && (
        <NodeConfigPanel
          selectedNode={{ id: selectedNode.id, ...selectedNode.data }}
          onClose={() => setSelectedNode(null)}
          onSave={handleSaveNodeConfig}
          onOpenChat={selectedNode.data.agentType === 'llm' ? handleOpenChat : undefined}
        />
      )}

      {showChat && chatConfig && (
        <ChatInterface
          nodeConfig={chatConfig}
          onClose={() => setShowChat(false)}
        />
      )}

      {showResults && (
        <ExecutionPanel
          results={executionResults}
          isRunning={isRunning}
          onClose={() => setShowResults(false)}
        />
      )}

      <PasswordModal
        isOpen={showPasswordModal}
        mode={passwordMode}
        onConfirm={handlePasswordConfirm}
        onCancel={() => setShowPasswordModal(false)}
      />
    </div>
  );
}

const executeButtonStyle = {
  padding: '8px 16px',
  backgroundColor: '#10b981',
  color: 'white',
  border: 'none',
  borderRadius: '4px',
  cursor: 'pointer',
  fontSize: '14px',
  fontWeight: '600',
};

const buttonStyle = {
  padding: '8px 16px',
  backgroundColor: '#1a192b',
  color: 'white',
  border: 'none',
  borderRadius: '4px',
  cursor: 'pointer',
  fontSize: '14px',
};

const encryptedButtonStyle = {
  padding: '8px 16px',
  backgroundColor: '#6366f1',
  color: 'white',
  border: 'none',
  borderRadius: '4px',
  cursor: 'pointer',
  fontSize: '14px',
  fontWeight: '600',
};

export default App;
